package com.pingone.loginapp.screens.main

import android.util.Base64
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.pingone.loginapp.data.*
import com.pingone.loginapp.data.Consts.Companion.BASIC
import com.pingone.loginapp.repository.auth.AuthRepository
import com.pingone.loginapp.screens.common.BaseViewModel
import com.pingone.loginapp.screens.common.LoginNavigation
import com.pingone.loginapp.util.oauth.Config
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    schedulersProvider: SchedulersProvider,
    private val config: Config
) : BaseViewModel(schedulersProvider) {

    val tokenInfoSubject: BehaviorSubject<List<Pair<String, String>>> = BehaviorSubject.create()
    val userInfoSubject: BehaviorSubject<List<Pair<String, String>>> = BehaviorSubject.create()
    val errorSubject: BehaviorSubject<String> = BehaviorSubject.create()

    fun proceedWithPKCEFlow(code: String) {
        compositeDisposable.add(
            config.readAuthConfig()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.backgroundScheduler)
                .flatMap {
                    proceedWithPKCE(code, it)
                }.flatMapCompletable {
                    authRepository.saveToken(it)
                }.subscribe({}, {
                    proceedWithError(it)
                })
        )
    }

    fun proceedWithFlow(code: String) {
        compositeDisposable.add(
            config.readAuthConfig()
                .subscribeOn(schedulersProvider.backgroundScheduler)
                .observeOn(schedulersProvider.backgroundScheduler)
                .flatMap {
                    when (it.tokenMethod) {
                        TokenMethod.CLIENT_SECRET_POST.stringValue -> proceedWithPost(code, it)
                        TokenMethod.CLIENT_SECRET_BASIC.stringValue -> proceedWithBasic(code, it)
                        else -> proceedWithPKCE(code, it)

                    }
                }.flatMapCompletable {
                    authRepository.saveToken(it)
                }.subscribe({}, {
                    proceedWithError(it)
                })
        )
    }

    private fun proceedWithPKCE(accessCode: String, configData: ConfigData): Flowable<AccessToken> {
        return authRepository.obtainAccessTokenPKCE(
            url = config.serverData!!.tokenEndpoint,
            clientId = configData.clientId,
            code_verifier = config.codeVerifier!!,
            code = accessCode,
            grantType = Consts.AUTHORIZATION_CODE,
            redirectUri = configData.redirectUri
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

    private fun showTokenInfo(data: List<Pair<String, String>>) {
        tokenInfoSubject.onNext(data)
    }

    private fun showUserInfo(data: List<Pair<String, String>>) {
        userInfoSubject.onNext(data)
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
                    showUserInfo(mapUserToPair(it.blockingFirst()))
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
                    showTokenInfo(mapTokenToPair(Gson().fromJson(jsonStr, TokenInfo::class.java)))
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

    private fun mapTokenToPair(token: TokenInfo): List<Pair<String, String>> {
        val list: MutableList<Pair<String, String>> = mutableListOf()

        val parser = JsonParser()
        val element = parser.parse(Gson().toJson(token))
        val obj = element.asJsonObject //since you know it's a JsonObject
        val entries = obj.entrySet()//will return members of your object
        for (entry in entries) {
            list.add(Pair(entry.key, entry.value.asJsonObject["value"].asString))
        }
        return list
    }

    private fun mapUserToPair(user: UserInfo): List<Pair<String, String>> {
        val list: MutableList<Pair<String, String>> = mutableListOf()

        val parser = JsonParser()
        val element = parser.parse(Gson().toJson(user))
        val obj = element.asJsonObject //since you know it's a JsonObject
        val entries = obj.entrySet()//will return members of your object
        for (entry in entries) {
            list.add(Pair(entry.key, entry.value.asString))
        }
        return list
    }

    private fun mapTokenClaims(claim: String) =
        when (claim) {
            "at_hash" -> "Access Token hash value."
            "sub" -> "User Identifier."
            "name" -> "User\"s full name."
            "given_name" -> "User given name(s) or first name(s)."
            "family_name" -> "Surname(s) or last name(s) of the User."
            "middle_name" -> "User middle name."
            "nickname" -> "User casual name."
            "preferred_username" -> "User shorthand name."
            "email" -> "User e-mail address."
            "updated_at" -> "Last time User\"s information was updated."
            "amr" -> "Authentication Methods Reference."
            "iss" -> "Response Issuer Identifier."
            "nonce" -> "Client session unique and random value."
            "aud" -> "ID Token Audience."
            "acr" -> "Authentication Context Class Reference."
            "auth_time" -> "User authentication time."
            "exp" -> "ID Toke expiration time."
            "iat" -> "Time at which the JWT was issued."
            "address_country" -> "Country name. "
            "address_postal_code" -> "Zip code or postal code. "
            "address_region" -> "State, province, prefecture, or region. "
            "address_locality" -> "City or locality. "
            "address_formatted" -> "Full mailing address. "
            "address_street_address" -> "Full street address. "
            "amr_0" -> "Authentication methods. "
            else -> claim
        }
}