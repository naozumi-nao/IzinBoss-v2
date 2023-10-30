package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences) : ViewModel() {

    val user = dataRepository.getUser()

    fun signOut() = dataRepository.signOut()
    fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.getAllLeaveRequests(companyId)
            emitSource(result.asLiveData())
        }
    }

    fun getCurrentUser() = dataRepository.getCurrentUserID()

    fun deleteCurrentUserPref() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.deleteCurrentUserPref()
        }
    }
}