package com.kouta.data.di

import android.content.Context
import androidx.room.Room
import com.kouta.data.vo.dao.SubscriptionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun providesSubscriptionDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, SubscriptionDatabase::class.java, "subscription_database").build()

    @Provides
    @Singleton
    fun providesSubscriptionDao(database: SubscriptionDatabase) = database.subscriptionDao()
}