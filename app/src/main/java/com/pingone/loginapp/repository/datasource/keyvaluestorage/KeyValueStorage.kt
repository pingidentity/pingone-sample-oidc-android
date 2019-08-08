package com.pingone.loginapp.repository.datasource.keyvaluestorage

interface KeyValueStorage {

  val token: String?

  val nonce: String?

  fun isUserLoggedIn(): Boolean

  fun onUserLoggedIn(sessionId: String)

  fun onGenerateNonce(nonce: String)

  fun logout()

}
