package com.pingone.loginapp.repository.datasource.keyvaluestorage

interface KeyValueStorage {

  val token: String?

  fun isUserLoggedIn(): Boolean

  fun onUserLoggedIn(sessionId: String)

  fun logout()

}
