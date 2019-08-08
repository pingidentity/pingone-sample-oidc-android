package com.pingone.loginapp.screens.main

import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.pingone.loginapp.repository.auth.AuthRepository
import com.pingone.loginapp.screens.common.BaseViewModel
import com.pingone.loginapp.screens.common.LoginNavigation
import com.pingone.loginapp.util.oauth.Config
import com.pingone.loginapp.util.oauth.TokenMethod
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    schedulersProvider: SchedulersProvider,
    private val config: Config
) : BaseViewModel(schedulersProvider) {

    fun proceedWithCode(intent: Intent) {
        val uri = Uri.parse(intent.dataString)
        val accessCode = uri.getQueryParameter("code")

        config.readAuthConfig()
            .subscribeOn(schedulersProvider.backgroundScheduler)
            .observeOn(schedulersProvider.backgroundScheduler)
            .map {
                authRepository.obtainAccessTokenPost(
                    config.serverData!!.token_endpoint,
                    it.client_id,
                    it.client_secret,
                    accessCode!!,
                    "authorization_code",
                    it.redirect_uri
                ).subscribe({
                    authRepository.saveToken(it.access_token)

                    authRepository.getUserInfo(config.serverData!!.userinfo_endpoint, "Bearer " + it.access_token)
                        .subscribe({
                            println(it)
                        },{
                            println(it)
                            proceedWithError()
                        })
                }, {
                    proceedWithError()
                })
            }.subscribe({

            }, {
                proceedWithError()
            })
    }

    fun obtainAccessToken() {

    }

    fun proceedWithFlow(intent: Intent) {
        val uri = Uri.parse(intent.dataString)
        val accessCode = uri.getQueryParameter("code")

        config.readAuthConfig()
            .subscribeOn(schedulersProvider.backgroundScheduler)
            .observeOn(schedulersProvider.backgroundScheduler)
            .map {
                when (it.token_method) {
                    TokenMethod.CLIENT_SECRET_POST.toString() -> {
                        authRepository.obtainAccessTokenPost(
                            config.serverData!!.token_endpoint,
                            it.client_id,
                            it.client_secret,
                            accessCode!!,
                            "authorization_code",
                            it.redirect_uri
                        ).subscribe()
                    }
                    TokenMethod.CLIENT_SECRET_BASIC.toString() -> {
                        authRepository.obtainAccessTokenBasic(
                            config.serverData!!.token_endpoint,
                            "ababagalamaga",
                            "authorization_code"
                        ).subscribe()
                    }
                    TokenMethod.NONE.toString() -> {
                        authRepository.obtainAccessTokenNone(
                            config.serverData!!.token_endpoint,
                            it.client_id,
                            accessCode!!,
                            "authorization_code",
                            it.redirect_uri
                        ).subscribe {
                            println()
                        }
                    }
                    else -> {
                        val credentials = it.client_id + ":" + it.client_secret
                        // create Base64 encodet string
                        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
                        authRepository.obtainAccessTokenBasic(
                            config.serverData!!.token_endpoint,
                            basic,
                            "authorization_code"
                        ).subscribe({
                            println(it)
                        }, {
                            println(it)
                        })
                    }
                }
            }.subscribe()
    }

    fun proceedWithError() {
        // back to logic activity if access token/ access code was not received
    }

    fun logout() {
        compositeDisposable.add(
            authRepository.logout()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.mainScheduler)
                .subscribe { navigation.postValue(LoginNavigation.Login) }
        )
    }
}