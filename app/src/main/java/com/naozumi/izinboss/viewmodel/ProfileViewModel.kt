package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.core.data.DataRepository

class ProfileViewModel(private val dataRepository: DataRepository): ViewModel() {
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    fun getCurrentUser() = dataRepository.getCurrentUser()
}