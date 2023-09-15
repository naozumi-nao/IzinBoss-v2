package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.flow.Flow

class MembersListViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    suspend fun getCompanyMembers(companyId: String, userRole: User.UserRole?) =
        dataRepository.getCompanyMembers(companyId, userRole)
    fun getCurrentUser() = dataRepository.getCurrentUser()
}