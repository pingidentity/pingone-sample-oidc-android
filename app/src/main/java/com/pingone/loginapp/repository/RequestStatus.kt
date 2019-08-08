package com.pingone.loginapp.repository

sealed class RequestStatus {
    object StatusInProgress : RequestStatus()
    object StatusFinished : RequestStatus()
    object StatusFailed : RequestStatus()
}