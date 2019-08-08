package com.pingone.loginapp.util.schedulers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DefaultSchedulersProvider : SchedulersProvider {

    override val singleScheduler = Schedulers.single()

    override val backgroundScheduler = Schedulers.io()

    override val mainScheduler = AndroidSchedulers.mainThread()

}