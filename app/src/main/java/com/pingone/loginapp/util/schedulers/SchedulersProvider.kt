package com.pingone.loginapp.util.schedulers

import io.reactivex.Scheduler

interface SchedulersProvider {

    val backgroundScheduler: Scheduler

    val mainScheduler: Scheduler

    val singleScheduler: Scheduler

}