package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.DataRepository

class MainViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun signOut() = dataRepository.signOut()
    suspend fun getAllLeaves() = dataRepository.getAllLeaves()
}