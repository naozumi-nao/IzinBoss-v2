package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.DataRepository

class MainViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun signOut() = dataRepository.signOut()
    suspend fun getAllLeaveRequests(companyId: String) = dataRepository.getAllLeaveRequests(companyId)
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    fun getCurrentUser() = dataRepository.getCurrentUser()
}