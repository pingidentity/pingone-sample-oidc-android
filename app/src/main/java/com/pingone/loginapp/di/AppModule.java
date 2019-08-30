package com.pingone.loginapp.di;

import android.content.Context;
import com.pingone.loginapp.app.LoginApp;
import dagger.Binds;
import dagger.Module;
import dagger.android.support.AndroidSupportInjectionModule;

@Module(includes = {AndroidSupportInjectionModule.class, InjectorsModule.class})
public abstract class AppModule {

    @Binds
    abstract Context applicationContext(LoginApp app);
}
