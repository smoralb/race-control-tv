package fr.groggy.racecontrol.tv.ui.channel

import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackRowPresenter

interface ChannelManager {
    fun addNewChannel(channelId: String)
    fun findPlaybackRowPresenter(glue: PlaybackTransportControlGlue<*>): PlaybackRowPresenter?
}
