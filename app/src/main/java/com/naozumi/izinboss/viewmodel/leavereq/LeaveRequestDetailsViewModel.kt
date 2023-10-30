package com.naozumi.izinboss.viewmodel.leavereq

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LeaveRequestDetailsViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
    fun changeLeaveRequestStatus(leaveRequest: LeaveRequest, isApproved: Boolean, managerName: String): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.changeLeaveRequestStatus(leaveRequest, isApproved, managerName)
            emitSource(result.asLiveData())
        }
    }

    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.deleteLeaveRequest(leaveRequest)
            emitSource(result.asLiveData())
        }
    }
}