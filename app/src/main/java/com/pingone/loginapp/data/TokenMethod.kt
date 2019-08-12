package com.pingone.loginapp.data

sealed class TokenMethod(val stringValue: String) {
    object CLIENT_SECRET_POST : TokenMethod("CLIENT_SECRET_POST")
    object CLIENT_SECRET_BASIC : TokenMethod("CLIENT_SECRET_BASIC")
    object NONE : TokenMethod("NONE")
}