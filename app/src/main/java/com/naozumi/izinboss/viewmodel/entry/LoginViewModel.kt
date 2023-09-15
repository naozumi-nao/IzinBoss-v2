package com.naozumi.izinboss.viewmodel.entry

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.repo.DataRepository

class LoginViewModel(private val dataRepository: DataRepository) : ViewModel() {

    suspend fun signInWithGoogle(userToken: String) = dataRepository.signInWithGoogle(userToken)

    suspend fun loginWithEmail(email: String, password: String) = dataRepository.loginWithEmail(email, password)

    fun getSignInIntent() = dataRepository.getSignInIntent()
}