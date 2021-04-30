package fr.groggy.racecontrol.tv.ui.channel.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.google.android.flexbox.FlexboxLayout
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.ui.channel.ChannelManager

@AndroidEntryPoint
class ChannelPlaybackActivity : FragmentActivity(), ChannelManager {

    companion object {
        private val TAG = ChannelPlaybackActivity::class.simpleName

        private val CHANNEL_ID = "${ChannelPlaybackActivity::class}.CHANNEL_ID"
        private val CONTENT_ID = "${ChannelPlaybackActivity::class}.CONTENT_ID"
        private val SESSION_ID = "${ChannelPlaybackActivity::class}.SESSION_ID"

        fun intent(
            context: Context,
            sessionId: String,
            channelId: String?,
            contentId: String
        ): Intent {
            return Intent(context, ChannelPlaybackActivity::class.java)
                .putExtra(SESSION_ID, sessionId)
                .putExtra(CONTENT_ID, contentId)
                .putExtra(CHANNEL_ID, channelId)
        }
    }

    private val channelGrid: FlexboxLayout by lazy {
        findViewById(R.id.channel_grid)
    }

    private lateinit var sessionId: String
    private lateinit var contentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_playback)

        sessionId = intent.getStringExtra(SESSION_ID) ?: return finish()
        contentId = intent.getStringExtra(CONTENT_ID) ?: return finish()

        attachChannel(channelId = null)
    }

    override fun addNewChannel(channelId: String) {
        channelGrid.children.forEach {
            it.updateLayoutParams<FlexboxLayout.LayoutParams> {
                flexBasisPercent = 0.5F
                flexShrink = 0.5F
            }
        }

        attachChannel(channelId)
    }

    private fun attachChannel(channelId: String?) {
        val viewId = View.generateViewId()
        channelGrid.addView(
            FragmentContainerView(this).apply {
                id = viewId
            },
            FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                flexBasisPercent = 0.5F
                flexShrink = 0.5F
            }
        )

        supportFragmentManager.beginTransaction()
            .add(viewId, ChannelPlaybackFragment.newInstance(sessionId, channelId, contentId), null)
            .commit()
    }
}
