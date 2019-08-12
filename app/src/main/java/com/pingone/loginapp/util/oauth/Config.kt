package com.pingone.loginapp.util.oauth

import android.content.Context
import com.google.gson.Gson
import com.pingone.loginapp.R
import com.pingone.loginapp.data.ConfigData
import com.pingone.loginapp.data.ServerConfig
import io.reactivex.Flowable
import java.io.IOException
import java.io.InputStream

class Config(private val context: Context) {

    private var cachedData: ConfigData? = null
    lateinit var serverData: ServerConfig
    lateinit var nonce: String

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
