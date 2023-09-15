package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.repo.DataRepository
import kotlinx.coroutines.launch

class CompanyViewModel(private val dataRepository: DataRepository): ViewModel() {
    suspend fun createCompany(name: String, userId: String) = dataRepository.createCompany(name, userId)
    fun getCurrentUser() = dataRepository.getCurrentUser()
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    suspend fun getCompanyData(companyId: String) = dataRepository.getCompanyData(companyId)

}