package com.pingone.loginapp.data

data class JWKS(
    val keys: List<Key>
)

data class Key(
    val e: String,
    val kid: String,
    val kty: String,
    val n: String,
    val use: String,
    val x5c: List<String>,
    val x5t: String
)