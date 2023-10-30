package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RequestLeaveViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences) : ViewModel() {
    suspend fun addLeaveRequest(companyId: String, leaveRequest: LeaveRequest): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.addLeaveRequest(companyId, leaveRequest)
            emitSource(result.asLiveData())
        }
    }

    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
}