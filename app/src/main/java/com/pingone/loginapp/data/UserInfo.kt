package com.pingone.loginapp.data

data class UserInfo(
    val address: Address,
    val email: String,
    val family_name: String,
    val given_name: String,
    val middle_name: String,
    val name: String,
    val preferred_username: String,
    val sub: String,
    val updated_at: Int
)

data class Address(
    val country: String,
    val locality: String,
    val postal_code: String,
    val region: String,
    val street_address: String
)