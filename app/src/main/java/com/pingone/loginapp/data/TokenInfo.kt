package com.pingone.loginapp.data

data class TokenInfo(
    val acr: Acr,
    val at_hash: AtHash,
    val aud: Aud,
    val auth_time: AuthTime,
    val email: Email,
    val exp: Exp,
    val given_name: GivenName,
    val iat: Iat,
    val iss: Iss,
    val nonce: Nonce,
    val preferred_username: PreferredUsername,
    val sub: Sub,
    val updated_at: UpdatedAt
)

data class Acr(
    val value: String
)

data class GivenName(
    val value: String
)

data class Exp(
    val value: Int
)

data class Iat(
    val value: Int
)

data class Nonce(
    val value: String
)

data class AtHash(
    val value: String
)

data class Sub(
    val value: String
)

data class Iss(
    val value: String
)

data class PreferredUsername(
    val value: String
)

data class Email(
    val value: String
)

data class UpdatedAt(
    val value: Int
)

data class Aud(
    val value: String
)

data class AuthTime(
    val value: Int
)