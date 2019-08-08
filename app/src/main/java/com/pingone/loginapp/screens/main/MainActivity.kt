package com.pingone.loginapp.screens.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pingone.loginapp.databinding.ActivityMainBinding
import com.pingone.loginapp.screens.auth.AuthActivity
import com.pingone.loginapp.screens.common.BaseActivity
import com.pingone.loginapp.screens.common.LoginNavigation
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, com.pingone.loginapp.R.layout.activity_main)
        binding.lifecycleOwner = this
        AndroidInjection.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.navigation.observe(this, Observer {
            when (it) {
                LoginNavigation.Login -> openScreenAndClearHistory(AuthActivity::class.java)
            }
        })
        binding.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        viewModel.proceedWithCode(intent)
    }
}
