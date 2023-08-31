package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.DataRepository

class CreateCompanyViewModel(private val dataRepository: DataRepository): ViewModel() {
    suspend fun createCompany(name: String, userId: String) = dataRepository.createCompany(name, userId)
}