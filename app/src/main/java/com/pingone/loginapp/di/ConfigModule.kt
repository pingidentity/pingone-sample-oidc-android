package com.pingone.loginapp.di

import android.content.Context
import com.pingone.loginapp.util.oauth.Config
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConfigModule {

    @Provides
    @Singleton
    fun providesConfig(context: Context) = Config(context)

}