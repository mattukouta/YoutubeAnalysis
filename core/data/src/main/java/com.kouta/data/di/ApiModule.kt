package com.kouta.data.di

import com.kouta.data.JsonAdapter
import com.kouta.data.RssService
import com.kouta.data.YoutubeService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun providesOkhttp() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }).build()

    @Provides
    @Singleton
    fun providesMoshi() = Moshi.Builder().add(JsonAdapter()).add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    @Named("youtube_data_api_retrofit")
    fun provideRetrofitYoutubeDataApi(okHttpClient: OkHttpClient, moshi: Moshi) =
        Retrofit
            .Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun providesTikXml() = TikXml.Builder().exceptionOnUnreadXml(false).build()

    @Provides
    @Singleton
    @Named("youtube_rss_api_retrofit")
    fun provideRetrofitYoutubeRss(okHttpClient: OkHttpClient, tilXml: TikXml) =
        Retrofit
            .Builder()
            .baseUrl("https://www.youtube.com/")
            .client(okHttpClient)
            .addConverterFactory(TikXmlConverterFactory.create(tilXml))
            .build()
    @Provides
    @Singleton
    fun providesYoutubeService(@Named("youtube_data_api_retrofit") retrofit: Retrofit) =
        retrofit.create(YoutubeService::class.java)

    @Provides
    @Singleton
    fun providesRssService(@Named("youtube_rss_api_retrofit") retrofit: Retrofit) =
        retrofit.create(RssService::class.java)
}