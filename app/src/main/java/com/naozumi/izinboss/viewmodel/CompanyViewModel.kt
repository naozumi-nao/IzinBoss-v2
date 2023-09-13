package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.core.data.DataRepository

class CompanyViewModel(private val dataRepository: DataRepository): ViewModel() {
    suspend fun createCompany(name: String, userId: String) = dataRepository.createCompany(name, userId)
    fun getCurrentUser() = dataRepository.getCurrentUser()
}