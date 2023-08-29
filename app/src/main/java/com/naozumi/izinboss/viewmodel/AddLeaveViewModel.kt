package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.DataRepository
import com.naozumi.izinboss.model.local.Leave

class AddLeaveViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun addLeaveToDatabase(leave: Leave) = dataRepository.addLeaveToDatabase(leave)
}