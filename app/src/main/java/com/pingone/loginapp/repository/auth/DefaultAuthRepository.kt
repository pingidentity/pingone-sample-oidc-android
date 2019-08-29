package com.pingone.loginapp.repository.auth

import com.pingone.loginapp.data.AccessToken
import com.pingone.loginapp.data.JWKS
import com.pingone.loginapp.data.UserInfo
import com.pingone.loginapp.repository.datasource.api.AuthService
import com.pingone.loginapp.repository.datasource.keyvaluestorage.KeyValueStorage
import com.pingone.loginapp.repository.datasource.room.RoomToken
import com.pingone.loginapp.repository.datasource.room.TokenDAO
import com.pingone.loginapp.util.oauth.Config
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.internal.operators.completable.CompletableFromAction

class DefaultAuthRepository(
    private val keyValueStorage: KeyValueStorage,
    private val service: AuthService,
    private val config: Config,
    private val tokenDAO: TokenDAO
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
        basicHeader: String,
        grantType: String,
        code: String,
        redirectUri: String
    ): Flowable<AccessToken> =
        service.obtainAccessTokenBasic(url, basicHeader, grantType, code, redirectUri).map { token -> token }

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

    override fun saveToken(token: AccessToken) = CompletableFromAction {
        tokenDAO.insertToken(
            RoomToken(
                accessToken = token.accessToken,
                tokenType = token.tokenType,
                expiresIn = token.expiresIn,
                scope = token.scope,
                idToken = token.idToken
            )
        )
    }

    override fun saveNonce(nonce: String) = CompletableFromAction { keyValueStorage.onGenerateNonce(nonce) }

    override fun isUserAuthenticated(): Single<Boolean> = tokenDAO.getToken()
        .map { true }
        .onErrorResumeNext { Single.just(false) }

    override fun logout() = Completable.fromAction {
        keyValueStorage.logout()
        tokenDAO.deleteAll()
    }

    override fun getAccessToken(): Single<AccessToken> = tokenDAO.getToken()
        .map {
            AccessToken(
                accessToken = it.accessToken,
                tokenType = it.tokenType,
                expiresIn = it.expiresIn,
                scope = it.scope,
                idToken = it.idToken

            )
        }

    override fun getJWKS(url: String): Flowable<JWKS> = service.getJWKS(url).map { jwks -> jwks }
}
