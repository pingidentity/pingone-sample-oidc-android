package com.pingone.loginapp.repository.auth

import com.pingone.loginapp.data.AccessToken
import com.pingone.loginapp.data.UserInfo
import io.reactivex.Completable
import io.reactivex.Flowable

interface AuthRepository {

    fun saveToken(token: String): Completable

    fun logout(): Completable

    fun isUserAvailable(): Boolean

    fun getInfo(url: String): Flowable<Any>

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
}