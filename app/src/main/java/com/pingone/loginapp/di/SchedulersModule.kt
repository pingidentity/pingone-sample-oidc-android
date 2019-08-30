package com.pingone.loginapp.di

import com.pingone.loginapp.util.schedulers.DefaultSchedulersProvider
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class SchedulersModule {
  @Provides @Singleton internal open fun provideSchedulersProvider(): SchedulersProvider =
      DefaultSchedulersProvider()
}