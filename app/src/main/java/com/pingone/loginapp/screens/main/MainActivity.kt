package com.pingone.loginapp.screens.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pingone.loginapp.R
import com.pingone.loginapp.data.Consts
import com.pingone.loginapp.databinding.ActivityMainBinding
import com.pingone.loginapp.screens.auth.AuthActivity
import com.pingone.loginapp.screens.common.BaseActivity
import com.pingone.loginapp.screens.common.LoginNavigation
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, com.pingone.loginapp.R.layout.activity_main)
        binding.lifecycleOwner = this
        AndroidInjection.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.navigation.observe(this, Observer {
            when (it) {
                LoginNavigation.Login -> openScreenAndClearHistory(AuthActivity::class.java)
            }
        })
        binding.viewModel = viewModel

        tokenInfoSubscriber()
        userInfoSubscriber()
        errorSubscriber()
    }

    override fun onStart() {
        super.onStart()
        intent.dataString?.let {
            var accessCode = ""
            Uri.parse(it).getQueryParameter(Consts.CODE).let { if (it != null) accessCode = it }
            if (accessCode.isBlank()) {
                showMessage(window.decorView.rootView, "Code is invalid")
                openScreenAndClearHistory(AuthActivity::class.java)
                return
            }
            viewModel.proceedWithFlow(accessCode)
        }
    }

    private fun tokenInfoSubscriber() {
        viewModel.tokenInfoSubject
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .map { showDialogWithJson(it, "Token info") }
            .subscribe()
    }

    private fun userInfoSubscriber() {
        viewModel.userInfoSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { showDialogWithJson(it, "User info") }
            .subscribe()
    }

    private fun errorSubscriber() {
        viewModel.errorSubject
            .map { showMessage(window.decorView.rootView, it) }
            .subscribe()
    }

    private fun showDialogWithJson(data: List<Pair<String, String>>, title: String) {
        val dialog = LayoutInflater.from(this).inflate(R.layout.dialog_data, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialog)
            .setTitle(title)


        val recyclerView = dialog.findViewById<RecyclerView>(R.id.raw_data)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val adapter = DataViewAdapter(data, LayoutInflater.from(this))
        recyclerView.setAdapter(adapter)

        builder.setNegativeButton("Close", { d, w -> d.dismiss() })
        builder.show()
    }
}
