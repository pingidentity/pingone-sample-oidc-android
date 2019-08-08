package com.pingone.loginapp.util.binding

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.BindingAdapter
import com.pingone.loginapp.repository.RequestStatus

class CustomBindings {

    @BindingAdapter("loadingVisibility")
    fun View.setRequestStatus(requestStatus: RequestStatus?) {
        this.visibility = if (requestStatus is RequestStatus.StatusInProgress) VISIBLE else GONE
    }

}