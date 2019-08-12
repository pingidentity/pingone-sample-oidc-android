package com.pingone.loginapp.data

import com.google.gson.annotations.SerializedName

data class BasicBody(
    @SerializedName("code") val code: String,
    @SerializedName("grant_type") val grantType: String,
    @SerializedName("redirect_uri") val redirectUri: String
)
