package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import kotlinx.coroutines.flow.Flow

class AddLeaveViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences) : ViewModel() {
    suspend fun addLeaveRequestToDatabase(companyId: String,leaveRequest: LeaveRequest) =
        dataRepository.addLeaveRequestToDatabase(companyId, leaveRequest)

    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
}