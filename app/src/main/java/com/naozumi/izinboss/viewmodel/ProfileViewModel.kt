package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.core.data.DataRepository
import com.naozumi.izinboss.core.data.UserPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    suspend fun deleteAccount(userId: String) = dataRepository.deleteAccount(userId)
    fun getCurrentUser() = dataRepository.getCurrentUser()

    fun deleteCurrentUserDataStore() {
        viewModelScope.launch {
            userPreferences.deleteCurrentUserDataStore()
        }
    }
}