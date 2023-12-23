package com.kouta.data.repository

import com.kouta.data.YoutubeServiceImpl
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.dao.SubscriptionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionVideoRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _totalResultsAvailable: MutableStateFlow<Int?> = MutableStateFlow(null)
    val totalResultsAvailable = _totalResultsAvailable.asStateFlow()

    private suspend fun getSubscriptionVideoTotalResultsAvailable(liveBroadcastContent: LiveBroadcastContent) = subscriptionDao.getTotalResultAvailable(liveBroadcastContent)

    fun loadSubscriptionVideos(liveBroadcastContent: LiveBroadcastContent) =
        if (liveBroadcastContent == LiveBroadcastContent.UNKNOWN) {
            subscriptionDao.getSubscriptionVideoAll()
        } else {
            subscriptionDao.getSubscriptionVideoFilterLiveBroadcastContent(liveBroadcastContent)
        }.also {
            coroutineScope.launch {
                _totalResultsAvailable.emit(getSubscriptionVideoTotalResultsAvailable(liveBroadcastContent))
            }
        }
}