package com.pingone.loginapp.screens.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.pingone.loginapp.R
import com.pingone.loginapp.databinding.ActivityAuthBinding
import com.pingone.loginapp.screens.common.BaseActivity
import com.pingone.loginapp.util.oauth.Config
import dagger.android.AndroidInjection
import javax.inject.Inject
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.AuthorizationRequest
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import com.pingone.loginapp.repository.auth.DefaultAuthRepository
import com.pingone.loginapp.screens.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import java.util.*


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
                    Uri.parse(config.serverData?.authorization_endpoint), // authorization endpoint
                    Uri.parse(config.serverData?.token_endpoint) // token endpoint
                )

                val authRequestBuilder = AuthorizationRequest.Builder(
                    serviceConfig, // the authorization service configuration
                    it.client_id, // the client ID, typically pre-registered and static
                    ResponseTypeValues.CODE, // the response_type value: we want a code
                    Uri.parse(it.redirect_uri) // the redirect URI to which the auth response is sent
                )

                val authRequest = authRequestBuilder
                    .setAdditionalParameters(mutableMapOf(Pair("nonce", config.nonce)))
                    .setScope(it.authorization_scope)
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
