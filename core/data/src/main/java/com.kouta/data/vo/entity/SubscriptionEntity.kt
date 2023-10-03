package com.kouta.data.vo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubscriptionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val imageUrl: String
)
