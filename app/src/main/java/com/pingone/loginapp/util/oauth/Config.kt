package com.pingone.loginapp.util.oauth

import android.content.Context
import com.google.gson.Gson
import com.pingone.loginapp.R
import io.reactivex.Flowable
import java.io.IOException
import java.io.InputStream

class Config(private val context: Context) {

    private var cachedData: ConfigData? = null
    var serverData: ServerConfig? = null
    var nonce: String? = null

    fun readAuthConfig(): Flowable<ConfigData> {
        return if (cachedData != null) {
            Flowable.just(cachedData)
        } else {
            Flowable.fromCallable { context.resources.openRawResource(R.raw.auth_config) }
                .map {
                    val configString = loadJSONFromAssets(it)
                    this.cachedData = Gson().fromJson(configString, ConfigData::class.java)
                    return@map cachedData
                }
        }
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

data class ConfigData(
    val environment_id: String,
    val client_id: String,
    val redirect_uri: String,
    val authorization_scope: String,
    val discovery_uri: String,
    val client_secret: String,
    val token_method: String
)

data class ServerConfig(
    val authorization_endpoint: String,
    val claim_types_supported: List<String>,
    val claims_parameter_supported: Boolean,
    val claims_supported: List<String>,
    val end_session_endpoint: String,
    val grant_types_supported: List<String>,
    val id_token_signing_alg_values_supported: List<String>,
    val issuer: String,
    val jwks_uri: String,
    val request_object_signing_alg_values_supported: List<String>,
    val request_parameter_supported: Boolean,
    val request_uri_parameter_supported: Boolean,
    val response_modes_supported: List<String>,
    val response_types_supported: List<String>,
    val scopes_supported: List<String>,
    val subject_types_supported: List<String>,
    val token_endpoint: String,
    val token_endpoint_auth_methods_supported: List<String>,
    val userinfo_endpoint: String,
    val userinfo_signing_alg_values_supported: List<String>
)

sealed class TokenMethod(val stringValue: String) {
    object CLIENT_SECRET_POST : TokenMethod("CLIENT_SECRET_POST")
    object CLIENT_SECRET_BASIC : TokenMethod("CLIENT_SECRET_BASIC")
    object NONE : TokenMethod("NONE")
}
