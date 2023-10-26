package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import kotlinx.coroutines.flow.Flow

class RequestLeaveViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences) : ViewModel() {
    suspend fun addLeaveRequest(companyId: String, leaveRequest: LeaveRequest) =
        dataRepository.addLeaveRequest(companyId, leaveRequest)

    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
}