package com.naozumi.izinboss.viewmodel.leavereq

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.flow.Flow

class LeaveRequestDetailsViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
    suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest, isApproved: Boolean, managerName: String) =
        dataRepository.changeLeaveRequestStatus(leaveRequest, isApproved, managerName)
    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?) = dataRepository.deleteLeaveRequest(leaveRequest)
}