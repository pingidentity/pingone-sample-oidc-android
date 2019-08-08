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

    override fun saveToken(token: AccessToken) = CompletableFromAction {
        tokenDAO.insertToken(
            RoomToken(
                access_token = token.access_token,
                token_type = token.token_type,
                expires_in = token.expires_in,
                scope = token.scope,
                id_token = token.id_token
            )
        )
    }

    override fun saveNonce(nonce: String) = CompletableFromAction { keyValueStorage.onGenerateNonce(nonce) }

    override fun isUserAvailable(): Single<Boolean> = tokenDAO.getToken()
        .map { true }
        .onErrorResumeNext { Single.just(false) }

    override fun logout() = Completable.fromAction { keyValueStorage.logout() }

    override fun getAccessToken(): Single<AccessToken> = tokenDAO.getToken()
        .map {
            AccessToken(
                access_token = it.access_token,
                token_type = it.token_type,
                expires_in = it.expires_in,
                scope = it.scope,
                id_token = it.id_token

            )
        }

    override fun getJWKS(url: String): Flowable<JWKS> = service.getJWKS(url).map { jwks -> jwks }
}
