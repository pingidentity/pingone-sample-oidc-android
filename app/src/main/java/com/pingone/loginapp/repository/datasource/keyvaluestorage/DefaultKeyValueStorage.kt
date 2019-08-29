package com.pingone.loginapp.repository.datasource.keyvaluestorage

import com.orhanobut.hawk.Hawk

class DefaultKeyValueStorage : KeyValueStorage {

    override val token: String?
        get() = Hawk.get(SESSION_ID)

    override val nonce: String?
        get() = Hawk.get(NONCE)

    override fun onGenerateNonce(nonce: String) {
        Hawk.put(NONCE, nonce)
    }

    override fun onUserLoggedIn(sessionId: String) {
        Hawk.put(SESSION_ID, sessionId)
    }

    override fun isUserLoggedIn(): Boolean = token != null

    override fun logout() {
        Hawk.delete(SESSION_ID)
        Hawk.delete(TOKEN)
    }

    private companion object {
        const val SESSION_ID = "session_id"
        const val TOKEN = "token"
        const val NONCE = "nonce"
    }
}