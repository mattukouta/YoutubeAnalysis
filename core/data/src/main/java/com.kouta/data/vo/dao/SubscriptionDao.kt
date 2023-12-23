package com.kouta.data.vo.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.Thumbnail
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.data.vo.entity.SubscriptionVideo
import com.kouta.data.vo.entity.VideoEntity
import com.kouta.data.vo.video.Video
import com.squareup.moshi.Moshi

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubscriptions(vararg subscriptionEntities: SubscriptionEntity)

    @Query("SELECT * From SubscriptionEntity")
    fun getSubscriptions(): PagingSource<Int, SubscriptionEntity>

    @Query("SELECT * From SubscriptionEntity")
    fun get(): List<SubscriptionEntity>

    @Delete
    suspend fun deleteSubscriptions(vararg subscriptionEntities: SubscriptionEntity)

    @Query("DELETE FROM SubscriptionEntity")
    suspend fun deleteAllSubscription()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(vararg videoEntity: VideoEntity)

    @Delete
    suspend fun deleteVideos(vararg searchEntities: VideoEntity)

    @Query("DELETE FROM VideoEntity")
    suspend fun deleteAllVideo()

    @Query("SELECT * FROM VideoEntity, SubscriptionEntity WHERE VideoEntity.channelId = SubscriptionEntity.id ORDER BY VideoEntity.`index` ASC")
    fun getSubscriptionVideoAll(): PagingSource<Int, SubscriptionVideo>

    @Query("SELECT * FROM VideoEntity, SubscriptionEntity WHERE VideoEntity.liveBroadcastContent = :liveBroadcastContent AND VideoEntity.channelId = SubscriptionEntity.id ORDER BY VideoEntity.`index` ASC")
    fun getSubscriptionVideoFilterLiveBroadcastContent(liveBroadcastContent: LiveBroadcastContent): PagingSource<Int, SubscriptionVideo>

    @Query("SELECT COUNT(*) FROM VideoEntity, SubscriptionEntity WHERE VideoEntity.liveBroadcastContent = :liveBroadcastContent AND VideoEntity.channelId = SubscriptionEntity.id")
   suspend fun getTotalResultAvailable(liveBroadcastContent: LiveBroadcastContent): Int
}

@Database(entities = [SubscriptionEntity::class, VideoEntity::class], version = 1, exportSchema = false)
@TypeConverters(SubscriptionConverter::class)
abstract class SubscriptionDatabase: RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}

class SubscriptionConverter {
    val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromSubscriptionEntityToString(data: SubscriptionEntity?): String? = data?.let {
        moshi.adapter(SubscriptionEntity::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToSubscriptionEntity(data: String?): SubscriptionEntity? = data?.let {
        moshi.adapter(SubscriptionEntity::class.java).fromJson(it)
    }

    @TypeConverter
    fun fromVideoEntityToString(data: VideoEntity?): String? = data?.let {
        moshi.adapter(VideoEntity::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToVideoEntity(data: String?): VideoEntity? = data?.let {
        moshi.adapter(VideoEntity::class.java).fromJson(it)
    }

    @TypeConverter
    fun fromSnippetToString(data: VideoEntity.Snippet?): String? = data?.let {
        moshi.adapter(VideoEntity.Snippet::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToSnippet(data: String?): VideoEntity.Snippet? = data?.let {
        moshi.adapter(VideoEntity.Snippet::class.java).fromJson(it)
    }

    @TypeConverter
    fun fromLiveStreamingDetailsToString(data: Video.Response.Video.LiveStreamingDetails?): String? = data?.let {
        moshi.adapter(Video.Response.Video.LiveStreamingDetails::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToLiveStreamingDetails(data: String?): Video.Response.Video.LiveStreamingDetails? = data?.let {
        moshi.adapter(Video.Response.Video.LiveStreamingDetails::class.java).fromJson(it)
    }

    @TypeConverter
    fun fromThumbnailsToString(data: Video.Response.Video.Snippet.Thumbnails?): String? = data?.let {
        moshi.adapter(Video.Response.Video.Snippet.Thumbnails::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToThumbnails(data: String?): Video.Response.Video.Snippet.Thumbnails? = data?.let {
        moshi.adapter(Video.Response.Video.Snippet.Thumbnails::class.java).fromJson(it)
    }

    @TypeConverter
    fun fromThumbnailToString(data: Thumbnail?): String? = data?.let {
        moshi.adapter(Thumbnail::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToThumbnail(data: String?): Thumbnail? = data?.let {
        moshi.adapter(Thumbnail::class.java).fromJson(it)
    }
}