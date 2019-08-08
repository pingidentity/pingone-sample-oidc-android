package com.pingone.loginapp.repository.auth

import com.pingone.loginapp.data.AccessToken
import com.pingone.loginapp.data.UserInfo
import com.pingone.loginapp.repository.datasource.api.AuthService
import com.pingone.loginapp.repository.datasource.keyvaluestorage.KeyValueStorage
import com.pingone.loginapp.util.oauth.Config
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.internal.operators.completable.CompletableFromAction

class DefaultAuthRepository(
    private val keyValueStorage: KeyValueStorage,
    private val service: AuthService,
    private val config: Config
) : AuthRepository {

    override fun readServerConfig(url: String) = service.getOauthConfig(url)
        .flatMapCompletable {
            config.storeConfig(it)
            Completable.complete()
        }!!

    override fun obtainAccessTokenPost(
        url: String,
        clientId: String,
        clientSecret: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flowable<AccessToken> =
        service.obtainAccessTokenPost(url, code, grantType, clientId, clientSecret, redirectUri).map { token -> token }

    override fun obtainAccessTokenBasic(
        url: String,
        base64Data: String,
        grantType: String
    ): Flowable<AccessToken> =
        service.obtainAccessTokenBasic(url, base64Data, grantType).map { token -> token }

    override fun obtainAccessTokenNone(
        url: String,
        clientId: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flowable<AccessToken> =
        service.obtainAccessTokenNone(url, code, grantType, clientId, redirectUri).map { token -> token }

    override fun getUserInfo(
        url: String,
        bearerToken: String
    ): Flowable<UserInfo> =
        service.getUserInfo(url, bearerToken).map { userInfo -> userInfo }

    override fun getInfo(
        url: String
    ): Flowable<Any> = service.getInfo(url).map { info -> info }

    override fun saveToken(token: String) = CompletableFromAction { keyValueStorage.onUserLoggedIn(token) }

    override fun isUserAvailable() = keyValueStorage.isUserLoggedIn()

    override fun logout() = Completable.fromAction { keyValueStorage.logout() }
}
