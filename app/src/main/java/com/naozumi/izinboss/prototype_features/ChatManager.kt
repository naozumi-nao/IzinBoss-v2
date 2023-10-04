package com.naozumi.izinboss.prototype_features

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

data class Message(
    val id: String,
    val content: String,
    val senderId: String,
    val receiverId: String,
    val timeStamp: Instant,
    val deliveryState: DeliveryState
)

enum class DeliveryState {
    UNDELIVERED,
    SENT,
    DELIVERED,
    READ
}

class ChatManager {

    private val messages = mutableListOf<Message>() // The list to store chat messages
    private val messagesMutex = Mutex() // Mutex for synchronizing access to the 'messages' list

    // Thread 1 adds "Message 1"
    // Thread 2 adds "Message 2"
    // Expected Result: ["Message 1", "Message 2"]
    // OR
    // ["Message 2", "Message 1"] IF Message 2 comes first
    suspend fun onMessageReceived(message: Message) {
        messagesMutex.withLock { // Acquiring the mutex to ensure thread safety
            messages.add(message) // Adding the message to the list within the mutex-protected block
            messages.sortBy { it.timeStamp } // Order by time
        }
    }

    // Thread 1 updates the state of a message
    // Thread 2 updates the state of another message
    // Expected Result: Updated message states
    // If one thread finishes first, it will not interfere with the other's update
    suspend fun onMessageDeliveryStateChanged(messageId: String, state: DeliveryState) {
        messagesMutex.withLock { // Acquiring the mutex to ensure thread safety
            val messageIndex = messages.indexOfFirst { it.id == messageId }

            if (messageIndex >= 0) {
                val message = messages[messageIndex]
                messages[messageIndex] = message.copy(deliveryState = state)
            }
        }
    }
}


// Thread 1 adds "Message 1"
// Thread 2 adds "Message 2"
// Expected Result: ["Message 1", "Message 2"]

// If thread 1 finishes first, thread 2 will overwrite list[0]
// and the list will become ["Message 2"]

/*
    private var messages = listOf<Message>() //initial empty list
    fun onMessageReceived(message: Message) {
        messages = messages + message
    }

    fun onMessageDeliveryStateChanged(messageId: String, state: DeliveryState) {
        messages = messages.map { message ->
            if (message.id == messageId) {
                message.copy(deliveryState = state)
            } else message
        }
    }

 */