package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.flow.Flow

class CompanyViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    suspend fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?) =
        dataRepository.createCompany(companyName, industrySector, user)
    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    suspend fun getCompanyData(companyId: String) = dataRepository.getCompanyData(companyId)
    suspend fun getCompanyMembers(companyId: String?) =
        dataRepository.getCompanyMembers(companyId)

}