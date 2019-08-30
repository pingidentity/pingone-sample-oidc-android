package com.pingone.loginapp.screens.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pingone.loginapp.util.schedulers.SchedulersProvider
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel(  val schedulersProvider: SchedulersProvider) : ViewModel() {

    val compositeDisposable = CompositeDisposable()
    val navigation = MutableLiveData<LoginNavigation>()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}