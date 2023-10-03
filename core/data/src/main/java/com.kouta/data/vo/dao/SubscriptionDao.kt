package com.kouta.data.vo.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.kouta.data.vo.entity.SubscriptionEntity

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg subscriptionEntities: SubscriptionEntity)

    @Query("SELECT * From SubscriptionEntity")
    fun get(): PagingSource<Int, SubscriptionEntity>

    @Delete
    suspend fun delete(vararg subscriptionEntities: SubscriptionEntity)

    @Query("DELETE FROM SubscriptionEntity")
    suspend fun deleteAll()
}

@Database(entities = [SubscriptionEntity::class], version = 1)
abstract class SubscriptionDatabase (): RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}