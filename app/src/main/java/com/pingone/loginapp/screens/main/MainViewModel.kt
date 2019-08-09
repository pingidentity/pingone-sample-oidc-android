package com.pingone.loginapp.screens.main

import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.pingone.loginapp.data.UserInfo
import com.pingone.loginapp.repository.auth.AuthRepository
import com.pingone.loginapp.screens.common.BaseViewModel
import com.pingone.loginapp.screens.common.LoginNavigation
import com.pingone.loginapp.util.oauth.Config
import com.pingone.loginapp.util.oauth.TokenMethod
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import javax.inject.Inject
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.pingone.loginapp.data.AccessToken
import com.pingone.loginapp.data.TokenInfo
import com.pingone.loginapp.util.oauth.ConfigData
import io.reactivex.Flowable

class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    schedulersProvider: SchedulersProvider,
    private val config: Config
) : BaseViewModel(schedulersProvider) {

    fun proceedWithFlow(intent: Intent) {
        val uri = Uri.parse(intent.dataString)
        val accessCode = uri.getQueryParameter("code")!!

        compositeDisposable.add(
            config.readAuthConfig()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.backgroundScheduler)
                .flatMap {
                    when (it.token_method) {
                        TokenMethod.CLIENT_SECRET_POST.stringValue -> proceedWithPost(accessCode, it)
                        TokenMethod.CLIENT_SECRET_BASIC.stringValue -> proceedWithBasic(it)
                        else -> proceedWithNone(accessCode, it)

                    }
                }.flatMapCompletable {
                    authRepository.saveToken(it)
                }.subscribe({}, { proceedWithError(it) })
        )
    }

    private fun proceedWithBasic(configData: ConfigData): Flowable<AccessToken> {
        val credentials = configData.client_id + ":" + configData.client_secret
        // create Base64 encodet string
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return authRepository.obtainAccessTokenBasic(
            config.serverData!!.token_endpoint,
            basic,
            "authorization_code"
        )
    }

    private fun proceedWithPost(accessCode: String, configData: ConfigData): Flowable<AccessToken> {
        return authRepository.obtainAccessTokenPost(
            config.serverData!!.token_endpoint,
            configData.client_id,
            configData.client_secret,
            accessCode,
            "authorization_code",
            configData.redirect_uri
        )
    }

    private fun proceedWithNone(accessCode: String, configData: ConfigData): Flowable<AccessToken> {
        return authRepository.obtainAccessTokenNone(
            config.serverData!!.token_endpoint,
            configData.client_id,
            accessCode,
            "authorization_code",
            configData.redirect_uri
        )
    }

    private fun proceedWithError(throwable: Throwable) {
        compositeDisposable.add(
            authRepository.logout()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.mainScheduler)
                .subscribe {
                    showErrorMessage(throwable.message)
                    navigation.postValue(LoginNavigation.Login)
                }
        )
    }

    private fun showTokenInfo(tokenInfo: TokenInfo) {
        //TODO: Display token info
    }

    private fun showUserInfo(userInfo: UserInfo) {
        //TODO: Display user info
    }

    private fun showErrorMessage(message: String?) {
        //TODO: Display user info
    }

    fun getUserInfo() {
        compositeDisposable.add(
            authRepository.getAccessToken()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .map { accessToken ->
                    authRepository.getUserInfo(
                        config.serverData!!.userinfo_endpoint,
                        accessToken.token_type + " " + accessToken.access_token
                    )
                        .map { showUserInfo(it) }.subscribe({}, { proceedWithError(it) })
                }
                .observeOn(schedulersProvider.mainScheduler)
                .subscribe({}, { proceedWithError(it) })
        )
    }

    fun getTokenInfo() {
        compositeDisposable.add(
            authRepository.getAccessToken()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.mainScheduler)
                .subscribe({
                    val jsonStr = Gson().toJson(JWT(it.id_token).claims)
                    showTokenInfo(Gson().fromJson(jsonStr, TokenInfo::class.java))
                }, {
                    proceedWithError(it)
                })
        )
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