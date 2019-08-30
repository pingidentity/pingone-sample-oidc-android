package com.pingone.loginapp.di

import android.content.Context
import androidx.room.Room
import com.pingone.loginapp.data.Consts.Companion.TOKEN
import com.pingone.loginapp.repository.datasource.room.TokenDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    internal fun provideTokenDao(tokenDatabase: TokenDatabase) = tokenDatabase.tokenDAO()

    @Provides
    @Singleton
    internal fun provideProducersDatabase(context: Context): TokenDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            TokenDatabase::class.java,
            TOKEN
        ).build()

}