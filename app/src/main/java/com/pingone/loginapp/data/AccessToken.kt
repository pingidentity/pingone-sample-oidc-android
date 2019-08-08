package com.pingone.loginapp.data

data class AccessToken(
    val access_token: String,
    val token_type: String,
    val expires_in: String,
    val scope: String,
    val id_token: String
)