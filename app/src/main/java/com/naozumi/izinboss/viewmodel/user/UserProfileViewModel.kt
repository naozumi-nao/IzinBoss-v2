package com.naozumi.izinboss.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserProfileViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
    fun getCompanyData(companyId: String): LiveData<Result<Company>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.getCompanyData(companyId)
            emitSource(result.asLiveData())
        }
    }
    fun changeFullName(newName: String, user: User?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.changeFullName(newName, user)
            emitSource(result.asLiveData())
        }
    }
    fun leaveCurrentCompany(userId: String?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.removeUserFromCompany(userId)
            emitSource(result.asLiveData())
        }
    }

    suspend fun deleteAccount(userId: String?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.deleteAccount(userId)
            emitSource(result.asLiveData())
        }
    }
}