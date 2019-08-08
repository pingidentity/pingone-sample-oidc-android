package com.pingone.loginapp.util.oauth

import android.content.Context
import com.pingone.loginapp.R
import io.reactivex.Flowable

class Config(private val context: Context) {

    private var cachedData: ConfigData? = null
    var serverData: ServerConfig? = null

    fun readAuthConfig(): Flowable<ConfigData> {
        return if (cachedData != null) {
            Flowable.just(cachedData)
        } else {
            Flowable.fromCallable { context.resources.openRawResource(R.raw.auth_config) }.map {
                this.cachedData = ConfigData()
                return@map cachedData
            }
        }
    }

    fun storeConfig(serverConfig: ServerConfig) {
        serverData = serverConfig
    }
}

data class ConfigData(
    val environment_id: String = "c2c2b4f8-c3da-4b23-abef-457ceaf25591",
    val client_id: String = "829528f0-46e3-4629-8192-3c8a7acf42e2",
    val redirect_uri: String = "com.example://redirect",
    val authorization_scope: String = "openid email profile p1:read:user",
    val discovery_uri: String = "https://auth.pingone.com/c2c2b4f8-c3da-4b23-abef-457ceaf25591/as/.well-known/openid-configuration",
    val client_secret: String = "~g7DmzhPdf62qf7BdzeYBJLIy6bHoqWsJO9k9O.e5u9VKIdIUyoozzR88dyXocEV",
    val token_method: String = "CLIENT_SECRET_POST"
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

sealed class TokenMethod {
    object CLIENT_SECRET_POST : TokenMethod()
    object CLIENT_SECRET_BASIC : TokenMethod()
    object NONE : TokenMethod()
}