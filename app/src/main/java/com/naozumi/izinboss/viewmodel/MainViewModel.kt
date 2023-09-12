package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.data.DataRepository
import com.naozumi.izinboss.data.UserPreferences
import com.naozumi.izinboss.model.local.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences) : ViewModel() {

    fun signOut() = dataRepository.signOut()
    suspend fun getAllLeaveRequests(companyId: String) = dataRepository.getAllLeaveRequests(companyId)
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    fun getCurrentUser() = dataRepository.getCurrentUser()

    fun saveUser(user: User) {
        viewModelScope.launch {
            userPreferences.saveUser(user)
        }
    }

    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userPreferences.deleteUser(user)
        }
    }
}