package com.naozumi.izinboss.viewmodel

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.data.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun signInWithGoogle(userToken: String) = userRepository.signInWithGoogle(userToken)

    fun loginWithEmail(email: String, password: String) = userRepository.loginWithEmail(email, password)

    fun getSignInIntent() = userRepository.getSignInIntent()
}