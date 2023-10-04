package com.naozumi.izinboss.prototype_features

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.locks.ReentrantLock

/*Pseudo Code
mutex = create_mutex()
queue = create_empty_queue()

Thread 1:
    enqueue(queue, Thread 1)
    lock(mutex)
    // Critical section
    unlock(mutex)

Thread 2:
    enqueue(queue, Thread 2)
    lock(mutex)
    // Critical section
    unlock(mutex)

// ...

Thread N:
    enqueue(queue, Thread N)
    lock(mutex)
    // Critical section
    unlock(mutex)

 */


// Create a mutex
val mutex = ReentrantLock()

// Create a FIFO queue to manage thread order
val queue = ArrayBlockingQueue<Runnable>(100)

// Worker thread
val workerThread = Thread {
    while (true) {
        try {
            // Dequeue a task from the queue
            val task = queue.take()

            // Lock the mutex
            mutex.lock()

            // Execute the critical section
            task.run()

            // Unlock the mutex
            mutex.unlock()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            break
        }
    }
}

/*
// Start the worker thread
workerThread.start()

// Enqueue tasks for execution (e.g., from multiple threads)
queue.put {
    // Critical section code for Task 1
    // ...
}

queue.put {
    // Critical section code for Task 2
    // ...
}

// ...

// Stop the worker thread when done (optional)
workerThread.interrupt()

 */
