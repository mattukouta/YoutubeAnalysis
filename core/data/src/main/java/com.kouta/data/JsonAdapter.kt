package com.kouta.data

import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.activities.ChannelActivities
import com.kouta.data.vo.subscriptions.Subscriptions
import com.squareup.moshi.FromJson

class JsonAdapter {
    @FromJson
    fun fromJsonToSnippetType(type: String) =
        ChannelActivities.Response.ActivityItem.Snippet.Type.entries.firstOrNull { it.value == type }
            ?: ChannelActivities.Response.ActivityItem.Snippet.Type.UNKNOWN

    @FromJson
    fun fromJsonToRecommendationReason(reason: String) =
        ChannelActivities.Response.ActivityItem.ContentDetails.Recommendation.Reason.entries.firstOrNull { it.value == reason }
            ?: ChannelActivities.Response.ActivityItem.ContentDetails.Recommendation.Reason.UNKNOWN

    @FromJson
    fun fromJsonToSocialType(type: String) =
        ChannelActivities.Response.ActivityItem.ContentDetails.Social.Type.entries.firstOrNull { it.value == type }
            ?: ChannelActivities.Response.ActivityItem.ContentDetails.Social.Type.UNKNOWN

    @FromJson
    fun fromJsonToSubscriptionType(type: String) =
        Subscriptions.Response.Subscription.ContentDetails.Type.entries.firstOrNull { it.value == type }
            ?: Subscriptions.Response.Subscription.ContentDetails.Type.UNKNOWN

    @FromJson
    fun fromJsonToLiveBroadcastContent(liveBroadcastContent: String) =
         LiveBroadcastContent.entries.firstOrNull { it.value == liveBroadcastContent }
            ?: LiveBroadcastContent.UNKNOWN
}