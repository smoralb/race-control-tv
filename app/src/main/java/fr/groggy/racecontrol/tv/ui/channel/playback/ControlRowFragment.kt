package fr.groggy.racecontrol.tv.ui.channel.playback

import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.*

class ControlRowFragment: PlaybackSupportFragment() {
    fun setUpAdapter(): PlaybackControlsRowPresenter {
        val playbackControlsRowPresenter = PlaybackControlsRowPresenter()
        playbackControlsRowPresenter.setSecondaryActionsHidden(false)
        val classPresenterSelector = ClassPresenterSelector()
        classPresenterSelector.addClassPresenter(
            PlaybackControlsRow::class.java,
            playbackControlsRowPresenter
        )
        val playbackControlsRow = PlaybackControlsRow()
        val rowsAdapter = ArrayObjectAdapter(classPresenterSelector)
        rowsAdapter.add(playbackControlsRow)
        adapter = rowsAdapter

        return playbackControlsRowPresenter
    }
}
