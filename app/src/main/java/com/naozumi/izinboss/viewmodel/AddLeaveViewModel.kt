package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.UserRepository
import com.naozumi.izinboss.model.local.Leave

class AddLeaveViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun addLeaveToDatabase(leave: Leave) = userRepository.addLeaveToDatabase(leave)
}