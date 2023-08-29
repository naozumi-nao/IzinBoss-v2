package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.DataRepository

class RegisterViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun registerWithEmail(name: String, email: String, password: String) = dataRepository.registerWithEmail(name, email, password)
}