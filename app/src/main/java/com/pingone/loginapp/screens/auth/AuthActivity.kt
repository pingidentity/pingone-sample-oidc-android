package com.pingone.loginapp.screens.auth

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.pingone.loginapp.R
import com.pingone.loginapp.databinding.ActivityAuthBinding
import com.pingone.loginapp.screens.common.BaseActivity
import com.pingone.loginapp.screens.main.MainActivity
import com.pingone.loginapp.util.oauth.Config
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import java.util.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), OauthClickHandler {

    @Inject
    lateinit var config: Config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAuthBinding>(this, R.layout.activity_auth)
        binding.lifecycleOwner = this
        binding.handler = this
        AndroidInjection.inject(this)
    }

    override fun onStart() {
        super.onStart()
        //read intent if case of login attempt error
    }

    @SuppressLint("CheckResult")
    // TODO: Remove oauth lib
    override fun startAuth() {
        config.nonce = UUID.randomUUID().toString()

        config.readAuthConfig()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val serviceConfig = AuthorizationServiceConfiguration(
                    Uri.parse(config.serverData?.authorizationEndpoint), // authorization endpoint
                    Uri.parse(config.serverData?.tokenEndpoint) // token endpoint
                )

                val authRequestBuilder = AuthorizationRequest.Builder(
                    serviceConfig, // the authorization service configuration
                    it.clientId, // the client ID, typically pre-registered and static
                    ResponseTypeValues.CODE, // the response_type value: we want a code
                    Uri.parse(it.redirectUri) // the redirect URI to which the auth response is sent
                )

                val authRequest = authRequestBuilder
                    .setAdditionalParameters(mutableMapOf(Pair("nonce", config.nonce)))
                    .setScope(it.authorizationScope)
                    .build()

                val authService = AuthorizationService(this)

                authService.performAuthorizationRequest(
                    authRequest,
                    PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0),
                    PendingIntent.getActivity(this, 0, Intent(this, AuthActivity::class.java), 0)
                )
            }, {
                println(it)
            }
            )
    }
}
