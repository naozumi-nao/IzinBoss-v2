package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.UserRepository

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun registerWithEmail(name: String, email: String, password: String) = userRepository.registerWithEmail(name, email, password)
}