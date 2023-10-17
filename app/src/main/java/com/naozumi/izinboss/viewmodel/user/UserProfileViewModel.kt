package com.naozumi.izinboss.viewmodel.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

class UserProfileViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
    suspend fun getCompanyData(companyId: String) = dataRepository.getCompanyData(companyId)
    suspend fun changeFullName(newName: String, user: User?) = dataRepository.changeFullName(newName, user)
    suspend fun leaveCurrentCompany(userId: String?) = dataRepository.kickUserFromCompany(userId)
    suspend fun deleteAccount(userId: String?) = dataRepository.deleteAccount(userId)
    fun deleteCurrentUserDataStore() {
        viewModelScope.launch {
            userPreferences.deleteCurrentUserDataStore()
        }
    }
}