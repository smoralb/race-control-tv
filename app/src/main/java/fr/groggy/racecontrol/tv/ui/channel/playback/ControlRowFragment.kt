package fr.groggy.racecontrol.tv.ui.channel.playback

import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.media.PlaybackBaseControlGlue
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*

class ControlRowFragment: PlaybackSupportFragment() {
    fun setUp(glue: PlaybackTransportControlGlue<*>): PlaybackRowPresenter {
//        val playbackControlsRowPresenter = rowPresenter(glue)
        val playbackControlsRowPresenter = PlaybackControlsRowPresenter(detailsPresenter())
        val rowsAdapter = ArrayObjectAdapter(playbackControlsRowPresenter)

        adapter = rowsAdapter

        return playbackControlsRowPresenter
    }

    private fun detailsPresenter(): AbstractDetailsDescriptionPresenter {
        return object : AbstractDetailsDescriptionPresenter() {
            override fun onBindDescription(viewHolder: ViewHolder, obj: Any) {
                val glue = obj as PlaybackBaseControlGlue<*>
                viewHolder.title.text = glue.title
                viewHolder.subtitle.text = glue.subtitle
            }
        }
    }

    private fun rowPresenter(glue: PlaybackTransportControlGlue<*>): PlaybackRowPresenter {
        val rowPresenter: PlaybackTransportRowPresenter = object : PlaybackTransportRowPresenter() {
            override fun onBindRowViewHolder(vh: RowPresenter.ViewHolder, item: Any) {
                super.onBindRowViewHolder(vh, item)
                vh.onKeyListener = glue
            }

            override fun onUnbindRowViewHolder(vh: RowPresenter.ViewHolder) {
                super.onUnbindRowViewHolder(vh)
                vh.onKeyListener = null
            }
        }
        rowPresenter.setDescriptionPresenter(detailsPresenter())
        return rowPresenter
    }
}
