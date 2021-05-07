package fr.groggy.racecontrol.tv.ui.channel.playback

import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.EventLogger
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.ViewingService
import fr.groggy.racecontrol.tv.f1tv.F1TvViewing
import fr.groggy.racecontrol.tv.ui.channel.ChannelManager
import fr.groggy.racecontrol.tv.ui.player.ExoPlayerPlaybackTransportControlGlue
import javax.inject.Inject

@Keep
@AndroidEntryPoint
class ChannelPlaybackFragment : VideoSupportFragment() {

    companion object {
        private val TAG = ChannelPlaybackFragment::class.simpleName

        private val CHANNEL_ID = "${ChannelPlaybackFragment::class}.CHANNEL_ID"
        private val CONTENT_ID = "${ChannelPlaybackFragment::class}.CONTENT_ID"
        private val SESSION_ID = "${ChannelPlaybackFragment::class}.SESSION_ID"

        fun newInstance(
            sessionId: String,
            channelId: String?,
            contentId: String
        ): ChannelPlaybackFragment {
            return ChannelPlaybackFragment().apply {
                arguments = bundleOf(
                    SESSION_ID to sessionId,
                    CHANNEL_ID to channelId,
                    CONTENT_ID to contentId
                )
            }
        }
    }

    @Inject lateinit var viewingService: ViewingService
    @Inject lateinit var httpDataSourceFactory: HttpDataSource.Factory

    private val trackSelector: DefaultTrackSelector by lazy {
        DefaultTrackSelector(requireContext())
    }

    private val player: SimpleExoPlayer by lazy {
        val player = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
        player.playWhenReady = true
        player.addAnalyticsListener(EventLogger(trackSelector))
        player
    }

    private val mediaSourceFactory: MediaSourceFactory by lazy {
        HlsMediaSource.Factory(httpDataSourceFactory)
            .setAllowChunklessPreparation(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        startPlayer()
    }

    private fun startPlayer() {
        val glue = ExoPlayerPlaybackTransportControlGlue(requireActivity(), player, trackSelector)
        glue.host = VideoSupportFragmentGlueHost(this)
        attachCustomControlRow(glue)

        val contentId = arguments?.getString(CONTENT_ID) ?: return requireActivity().finish()
        val channelId = arguments?.getString(CHANNEL_ID)
        lifecycleScope.launchWhenStarted {
            try {
                val viewing = viewingService.getViewing(channelId, contentId)
                onViewingCreated(viewing)
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    private fun attachCustomControlRow(glue: PlaybackTransportControlGlue<*>) {
        val manager = activity as? ChannelManager
        val presenter = manager?.findPlaybackRowPresenter(glue)
        if (presenter != null) {
            setPlaybackRowPresenter(presenter)
        }
    }

    private fun handleError() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setMessage(R.string.unable_to_play_video_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requireActivity().finish()
            }
    }

    private fun onViewingCreated(viewing: F1TvViewing) {
        Log.d("ChannelPlayback", "Viewing is ready $viewing")
        if (!player.isPlaying) { //IF is playing already just ignore these calls
            val mediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(viewing.url))
            player.setMediaSource(mediaSource)
            player.prepare()
        }
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        player.release()
        super.onDestroy()
    }
}
