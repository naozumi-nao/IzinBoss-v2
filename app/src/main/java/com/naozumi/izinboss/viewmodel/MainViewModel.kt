package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.UserRepository

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun signOut() = userRepository.signOut()
}