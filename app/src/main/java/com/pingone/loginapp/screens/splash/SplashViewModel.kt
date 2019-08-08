package com.pingone.loginapp.screens.splash

import com.pingone.loginapp.repository.auth.AuthRepository
import com.pingone.loginapp.screens.common.BaseViewModel
import com.pingone.loginapp.screens.common.LoginNavigation
import com.pingone.loginapp.util.oauth.Config
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import javax.inject.Inject


class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    schedulersProvider: SchedulersProvider,
    private val config: Config
) : BaseViewModel(schedulersProvider) {

    fun start() {
        compositeDisposable.add(
            config.readAuthConfig()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .flatMapCompletable {
                    authRepository.readServerConfig(it.discovery_uri)
                }
                .observeOn(schedulersProvider.backgroundScheduler)
                .subscribe({
                    navigation.postValue(LoginNavigation.Login)
                }, {
                    // Display error
                })
        )
    }
}