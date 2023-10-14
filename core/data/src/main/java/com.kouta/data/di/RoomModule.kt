package com.kouta.data.di

import android.content.Context
import androidx.room.Room
import com.kouta.data.vo.dao.SearchDatabase
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
    ) = Room.inMemoryDatabaseBuilder(context, SubscriptionDatabase::class.java).build()

    @Provides
    @Singleton
    fun providesSubscriptionDao(database: SubscriptionDatabase) = database.subscriptionDao()

    @Provides
    @Singleton
    fun providesSearchVideoDatabase(
        @ApplicationContext context: Context
    ) = Room.inMemoryDatabaseBuilder(context, SearchDatabase::class.java).build()

    @Provides
    @Singleton
    fun providesSearchVideoDao(database: SearchDatabase) = database.searchVideoDao()
}