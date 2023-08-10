package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.UserRepository

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun registerWithEmail(email: String, password: String) = userRepository.registerWithEmail(email, password)
}