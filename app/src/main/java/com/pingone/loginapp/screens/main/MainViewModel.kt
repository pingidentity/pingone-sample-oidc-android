package com.pingone.loginapp.screens.main

import android.net.Uri
import android.util.Base64
import com.pingone.loginapp.repository.auth.AuthRepository
import com.pingone.loginapp.screens.common.BaseViewModel
import com.pingone.loginapp.screens.common.LoginNavigation
import com.pingone.loginapp.util.oauth.Config
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import javax.inject.Inject
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.pingone.loginapp.data.*
import com.pingone.loginapp.data.Consts.Companion.BASIC
import com.pingone.loginapp.data.Consts.Companion.CODE
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import android.R.attr.password


class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    schedulersProvider: SchedulersProvider,
    private val config: Config
) : BaseViewModel(schedulersProvider) {

    val tokenInfoSubject: BehaviorSubject<TokenInfo> = BehaviorSubject.create()
    val userInfoSubject: BehaviorSubject<UserInfo> = BehaviorSubject.create()
    val errorSubject: BehaviorSubject<String> = BehaviorSubject.create()

    fun proceedWithFlow(code: String) {
        compositeDisposable.add(
            config.readAuthConfig()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.backgroundScheduler)
                .flatMap {
                    when (it.tokenMethod) {
                        TokenMethod.CLIENT_SECRET_POST.stringValue -> proceedWithPost(code, it)
                        TokenMethod.CLIENT_SECRET_BASIC.stringValue -> proceedWithBasic(code, it)
                        else -> proceedWithNone(code, it)

                    }
                }.flatMapCompletable {
                    authRepository.saveToken(it)
                }.subscribe({}, {
                    proceedWithError(it)
                })
        )
    }

    private fun proceedWithBasic(accessCode: String, configData: ConfigData): Flowable<AccessToken> {
        val credentials = configData.clientId + ":" + configData.clientSecret
        val basic = BASIC + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return authRepository.obtainAccessTokenBasic(
            url = config.serverData!!.tokenEndpoint,
            basicHeader = basic,
            grantType = Consts.AUTHORIZATION_CODE,
            code = accessCode,
            redirectUri = configData.redirectUri
        )
    }

    private fun proceedWithPost(accessCode: String, configData: ConfigData): Flowable<AccessToken> {
        return authRepository.obtainAccessTokenPost(
            url = config.serverData!!.tokenEndpoint,
            clientId = configData.clientId,
            clientSecret = configData.clientSecret,
            grantType = accessCode,
            code = Consts.AUTHORIZATION_CODE,
            redirectUri = configData.redirectUri
        )
    }

    private fun proceedWithNone(accessCode: String, configData: ConfigData): Flowable<AccessToken> {
        return authRepository.obtainAccessTokenNone(
            url = config.serverData!!.tokenEndpoint,
            clientId = configData.clientId,
            grantType = accessCode,
            code = Consts.AUTHORIZATION_CODE,
            redirectUri = configData.redirectUri
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
        tokenInfoSubject.onNext(tokenInfo)
    }

    private fun showUserInfo(userInfo: UserInfo) {
        userInfoSubject.onNext(userInfo)
    }

    private fun showErrorMessage(message: String?) {
        errorSubject.onNext(message.let { "Something went wrong" })
    }

    fun getUserInfo() {
        compositeDisposable.add(
            authRepository.getAccessToken()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .map { accessToken ->
                    authRepository.getUserInfo(
                        config.serverData!!.userinfoEndpoint,
                        accessToken.tokenType + " " + accessToken.accessToken
                    )
                }
                .map {
                    showUserInfo(it.blockingFirst())
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
                    val jsonStr = Gson().toJson(JWT(it.idToken).claims)
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