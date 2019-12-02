package com.pingone.loginapp.util.oauth

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.pingone.loginapp.R
import com.pingone.loginapp.data.ConfigData
import com.pingone.loginapp.data.ServerConfig
import io.reactivex.Flowable
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom

class Config(private val context: Context) {

    private var cachedData: ConfigData? = null
    var serverData: ServerConfig? = null
    var nonce: String? = null
    var codeVerifier: String? = null

    fun readAuthConfig(): Flowable<ConfigData> {
        return if (cachedData != null) {
            Flowable.just(cachedData)
        } else {
            Flowable.fromCallable { context.resources.openRawResource(R.raw.auth_config) }
                .map {
                    codeVerifier = generateCodeVerifier()
                    val configString = loadJSONFromAssets(it)
                    this.cachedData = Gson().fromJson(configString, ConfigData::class.java)
                    return@map cachedData
                }
        }
    }

    private fun generateCodeVerifier(): String {
        val sr = SecureRandom()
        val code = ByteArray(32)
        sr.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun loadJSONFromAssets(inputStream: InputStream): String? {
        var json: String? = null
        try {
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            json = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream.close()
        }
        return json
    }

    fun storeConfig(serverConfig: ServerConfig) {
        serverData = serverConfig
    }
}
