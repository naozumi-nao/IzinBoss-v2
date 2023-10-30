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

class MainViewModel(private val dataRepository: DataRepository) : ViewModel() {

    val user = dataRepository.getUser()

    val userData: LiveData<User> = liveData(Dispatchers.Main) {
        val user = dataRepository.getUserData(getCurrentUser())
        if (user != null) {
            emit(user)
        }
    }

    fun signOut() = dataRepository.signOut()
    fun getAllLeaveRequests(): LiveData<Result<List<LeaveRequest>>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.getAllLeaveRequests(user)
            emitSource(result.asLiveData())
        }
    }

    fun getCurrentUser() = dataRepository.getCurrentUserID()

    fun saveUserToPreferences(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.saveUserToPreferences(user)
        }
    }

    fun deleteCurrentUserPref() {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteCurrentUserFromPreferences()
        }
    }
}