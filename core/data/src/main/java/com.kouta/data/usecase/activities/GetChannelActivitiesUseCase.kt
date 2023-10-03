package com.kouta.data.usecase.activities

import com.kouta.data.repository.ChannelRepository
import com.kouta.data.vo.activities.ChannelActivities
import javax.inject.Inject

class GetChannelActivitiesUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {
    suspend fun getChannelActivities(query: ChannelActivities.RequestQuery) =
        channelRepository.getActivities(queryMap = query.toQueryMap())
}