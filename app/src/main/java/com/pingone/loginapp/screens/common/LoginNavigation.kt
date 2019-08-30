package com.pingone.loginapp.screens.common

sealed class LoginNavigation {

    object Login : LoginNavigation()
    object Main : LoginNavigation()

}