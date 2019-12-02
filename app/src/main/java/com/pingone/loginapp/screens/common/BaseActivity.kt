package com.pingone.loginapp.screens.common

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity() {

    protected fun openScreenAndClearHistory(destinationActivity: Class<out Activity>) {
        val intent = Intent(this, destinationActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //clear back stack
        startActivity(intent)
        finish()
    }

    protected fun showMessage(view: View, msg: String) {
        view.let {
            Snackbar.make(
                it,
                msg,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
