package com.naozumi.izinboss.model.helper

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment() // Set app as busy
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle
    }
}