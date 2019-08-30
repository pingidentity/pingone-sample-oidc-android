package com.pingone.loginapp.screens.splash

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pingone.loginapp.R
import com.pingone.loginapp.databinding.ActivitySplashBinding
import com.pingone.loginapp.screens.auth.AuthActivity
import com.pingone.loginapp.screens.common.BaseActivity
import com.pingone.loginapp.screens.common.LoginNavigation
import com.pingone.loginapp.screens.main.MainActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)
        binding.lifecycleOwner = this
        AndroidInjection.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SplashViewModel::class.java)

        viewModel.navigation.observe(this, Observer {
            openScreenAndClearHistory(
                when (it) {
                    LoginNavigation.Main -> MainActivity::class.java
                    LoginNavigation.Login -> AuthActivity::class.java
                }
            )
        })

        viewModel.start()
    }
}
