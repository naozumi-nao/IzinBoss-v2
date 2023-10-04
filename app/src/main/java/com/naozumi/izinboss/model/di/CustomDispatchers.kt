package com.naozumi.izinboss.model.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CustomDispatchers {
    val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default // General-purpose operations
    val dispatcherIO: CoroutineDispatcher = Dispatchers.IO // Network & Database
    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main // UI-Thread Only
    val dispatcherUnconfined: CoroutineDispatcher = Dispatchers.Unconfined // Intercept non-suspending call
}