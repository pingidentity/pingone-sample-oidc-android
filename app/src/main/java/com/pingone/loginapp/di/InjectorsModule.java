package com.pingone.loginapp.di;

import com.pingone.loginapp.screens.auth.AuthActivity;
import com.pingone.loginapp.screens.main.MainActivity;
import com.pingone.loginapp.screens.splash.SplashActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class InjectorsModule {

    @ContributesAndroidInjector
    abstract SplashActivity splashActivityInjector();

    @ContributesAndroidInjector
    abstract AuthActivity authActivity();

    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

}
