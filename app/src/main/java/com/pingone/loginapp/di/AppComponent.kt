package com.pingone.loginapp.di

import com.pingone.loginapp.app.LoginApp
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        NetworkModule::class,
        DataModule::class,
        SchedulersModule::class,
        ViewModelModule::class,
        RoomModule::class,
        AppModule::class,
        ConfigModule::class]
)
interface AppComponent : AndroidInjector<LoginApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<LoginApp>()

}
