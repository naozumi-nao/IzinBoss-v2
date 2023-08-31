package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.DataRepository
import com.naozumi.izinboss.model.local.LeaveRequest

class AddLeaveViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun addLeaveRequestToDatabase(companyId: String,leaveRequest: LeaveRequest) = dataRepository.addLeaveRequestToDatabase(companyId, leaveRequest)
}