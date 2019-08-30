package com.pingone.loginapp.di

import com.pingone.loginapp.repository.auth.AuthRepository
import com.pingone.loginapp.repository.auth.DefaultAuthRepository
import com.pingone.loginapp.repository.datasource.api.AuthService
import com.pingone.loginapp.repository.datasource.keyvaluestorage.DefaultKeyValueStorage
import com.pingone.loginapp.repository.datasource.keyvaluestorage.KeyValueStorage
import com.pingone.loginapp.repository.datasource.room.TokenDAO
import com.pingone.loginapp.util.oauth.Config
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    internal fun provideKeyValueStorage(): KeyValueStorage = DefaultKeyValueStorage()

    @Provides
    @Singleton
    internal fun provideAuthRepository(
        keyValueStorage: KeyValueStorage,
        authService: AuthService,
        config: Config,
        tokenDAO: TokenDAO
    ): AuthRepository =
        DefaultAuthRepository(keyValueStorage, authService, config, tokenDAO)

}
