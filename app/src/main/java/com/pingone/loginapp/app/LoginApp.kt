package com.pingone.loginapp.app

import com.orhanobut.hawk.Hawk
import com.pingone.loginapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class LoginApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}