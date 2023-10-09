package com.kouta.data.vo.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SubscriptionVideo(
    @Embedded
    val video: VideoEntity,

    @Relation(
        parentColumn = "channelId",
        entityColumn = "id"
    )
    val subscription: SubscriptionEntity
)
