package com.pingone.loginapp.app

import androidx.databinding.DataBindingUtil
import com.orhanobut.hawk.Hawk
import com.pingone.loginapp.util.binding.BindingComponent
import com.pingone.loginapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class LoginApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this)
            .build()
        DataBindingUtil.setDefaultComponent(BindingComponent())

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}