package com.naozumi.izinboss.prototype_features

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

val forkMutexes = List(5) { Mutex() } // Mutex represent forks

suspend fun philosopher(id: Int) {
    while (true) {
        think(id)
        eat(id)
    }
}

suspend fun think(id: Int) { // Simulate philosopher thinking for random amount of time (1 - 3 secs)
    println("Philosopher $id is thinking...")
    delay((1..3).random() * 1000L)
}

suspend fun eat(id: Int) { // Simulate philosopher eating with 2 forks
    val leftFork = forkMutexes[id] // left fork pos identical to the philosopher id
    val rightFork = forkMutexes[(id + 1) % 5] // right fork pos identical to the philosopher id + 1
                                                    // %5 to reset pos (so it doesn't get more than 0..4)

    leftFork.lock() // lock left fork
    rightFork.lock() // lock right fork

    try {
        println("Philosopher $id is eating...")
        delay((1..3).random() * 1000L) // randomized eating time (1 - 3 secs)
    } finally {
        leftFork.unlock() // release left fork lock
        rightFork.unlock() // release right fork lock
    }
}

fun main() = runBlocking {
    val philosophers = List(5) { id ->
        launch { philosopher(id) }
    }

    delay(10000) // Let philosophers eat for 10 seconds

    philosophers.forEach { it.cancel() } // Force end simulation

    delay(1000) // Cleaning up for 1 second
}