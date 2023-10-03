package com.kouta.data.usecase.channels

import com.kouta.data.repository.ChannelRepository
import com.kouta.data.vo.activities.ChannelActivities
import com.kouta.data.vo.channels.Channels
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {
    suspend fun get(query: Channels.RequestQuery) = channelRepository.getChannels(
        query.toQueryMap()
    )


}