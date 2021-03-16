package fr.groggy.racecontrol.tv.core.channel

import android.util.Log
import fr.groggy.racecontrol.tv.core.driver.DriverService
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannel
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType
import fr.groggy.racecontrol.tv.f1tv.F1TvChannel
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelService @Inject constructor(
    private val repository: ChannelRepository,
    private val f1Tv: F1TvClient,
    private val driverService: DriverService
) {

    companion object {
        private val TAG = ChannelService::class.simpleName
    }

    suspend fun loadChannelsWithDrivers(contentId: String) {
        Log.d(TAG, "loadChannelsWithDrivers")
        val channels = f1Tv.getChannels(contentId)
        repository.save(listOf(basicChannel(contentId)) + channels)
        //channels.forEach { if (it is F1TvOnboardChannel) driverService.loadDriverWithImages(it.driver) }
    }

    private fun basicChannel(contentId: String): F1TvChannel {
        return F1TvBasicChannel(
            channelId = contentId,
            contentId = contentId,
            F1TvBasicChannelType.Companion.Wif
        )
    }
}
