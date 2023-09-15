package com.naozumi.izinboss.viewmodel.entry

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.repo.DataRepository

class RegisterViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun registerWithEmail(name: String, email: String, password: String) = dataRepository.registerWithEmail(name, email, password)
}