package com.pingone.loginapp.repository.auth

import com.pingone.loginapp.data.AccessToken
import com.pingone.loginapp.data.JWKS
import com.pingone.loginapp.data.UserInfo
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface AuthRepository {

    fun saveToken(token: AccessToken): Completable

    fun saveNonce(nonce: String): Completable

    fun logout(): Completable

    fun isUserAvailable(): Single<Boolean>

    fun getAccessToken(): Single<AccessToken>

    fun readServerConfig(url: String): Completable

    fun obtainAccessTokenPost(
        url: String,
        clientId: String,
        clientSecret: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flowable<AccessToken>

    fun obtainAccessTokenNone(
        url: String,
        clientId: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flowable<AccessToken>

    fun obtainAccessTokenBasic(
        url: String,
        base64Data: String,
        grantType: String
    ): Flowable<AccessToken>

    fun getUserInfo(
        url: String,
        bearerToken: String
    ): Flowable<UserInfo>

    fun getJWKS(
        url: String
    ): Flowable<JWKS>
}