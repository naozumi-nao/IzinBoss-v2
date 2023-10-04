package com.naozumi.izinboss.prototype_features

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// ensures that the provided action is executed only if at least 2 seconds have passed
// since the last network request. It uses a mutex (Mutex) to achieve mutual exclusion,
// preventing multiple requests from being executed simultaneously.
// TODO: This hasn't worked well yet, I'm still figuring out how to make it work

object NetworkDebounce {
    private val debounceMutex = Mutex()
    private var job: Job? = null

    suspend fun debounceNetworkCall(action: suspend () -> Unit) {
        debounceMutex.withLock {
            job?.cancel() // Cancel the previous job if it exists

            // coroutine scope tied to the lifecycle of component
            val scope = CoroutineScope(Dispatchers.Main)

            // Start a new coroutine with a delay of 2 seconds
            job = scope.launch {
                delay(2000)
                action()
            }
        }
    }
}
