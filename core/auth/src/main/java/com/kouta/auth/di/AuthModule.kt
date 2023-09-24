package com.kouta.auth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    fun provideDataStore(
        @ApplicationContext context: Context,
        serializer: EncryptedAuthStateSerializer
    ): DataStore<AuthState> = DataStoreFactory.create(serializer = serializer) {
        context.dataStoreFile("auth.pb")
    }
}