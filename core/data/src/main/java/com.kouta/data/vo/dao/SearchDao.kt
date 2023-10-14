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
import com.kouta.data.vo.Thumbnail
import com.kouta.data.vo.entity.SearchVideoEntity
import com.kouta.data.vo.search.Search.Response.SearchItems.Snippet.Thumbnails
import com.squareup.moshi.Moshi

@Dao
interface SearchVideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg searchEntities: SearchVideoEntity)

    @Query("SELECT * FROM SearchVideoEntity")
    fun get(): PagingSource<Int, SearchVideoEntity>

    @Delete
    suspend fun delete(vararg searchEntities: SearchVideoEntity)

    @Query("DELETE FROM SearchVideoEntity")
    suspend fun deleteAll()
}

@Database(entities = [SearchVideoEntity::class], version = 1, exportSchema = false)
@TypeConverters(SearchConverter::class)
abstract class SearchDatabase() : RoomDatabase() {
    abstract fun searchVideoDao(): SearchVideoDao
}

class SearchConverter {
    val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromThumbnailsToString(data: Thumbnails?): String? = data?.let {
        moshi.adapter(Thumbnails::class.java).toJson(it)
    }

    @TypeConverter
    fun fromStringToThumbnails(data: String?): Thumbnails? = data?.let {
        moshi.adapter(Thumbnails::class.java).fromJson(it)
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