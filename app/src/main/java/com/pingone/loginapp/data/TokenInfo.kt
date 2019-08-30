package com.pingone.loginapp.data

import com.google.gson.annotations.SerializedName

data class TokenInfo(
    @SerializedName("acr") val acr: Acr,
    @SerializedName("at_hash") val atHash: AtHash,
    @SerializedName("aud") val aud: Aud,
    @SerializedName("auth_time") val authTime: AuthTime,
    @SerializedName("email") val email: Email,
    @SerializedName("exp") val exp: Exp,
    @SerializedName("given_name") val givenName: GivenName,
    @SerializedName("iat") val iat: Iat,
    @SerializedName("iss") val iss: Iss,
    @SerializedName("nonce") val nonce: Nonce,
    @SerializedName("preferred_username") val preferredUsername: PreferredUsername,
    @SerializedName("sub") val sub: Sub,
    @SerializedName("updated_at") val updatedAt: UpdatedAt
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