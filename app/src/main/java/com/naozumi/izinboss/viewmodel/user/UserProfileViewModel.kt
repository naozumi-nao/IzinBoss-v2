package com.naozumi.izinboss.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.wrapIdlingResource
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserProfileViewModel(private val dataRepository: DataRepository): ViewModel() {
    val user= runBlocking {
        dataRepository.getUser()
    }
    fun getUserData(): LiveData<Result<User>> = liveData(Dispatchers.Main) {
        wrapIdlingResource {
            emit(Result.Loading)
            try {
                val result = dataRepository.getUserData(user?.uid)
                if (result != null) {
                    emit(Result.Success(result))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.toString()))
            }
        }
    }

    fun updateLocalUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.saveUserToPreferences(user)
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

    fun deleteAccount(userId: String?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.deleteAccount(userId)
            emitSource(result.asLiveData())
        }
    }
}