package com.naozumi.izinboss.viewmodel.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.naozumi.izinboss.model.helper.Result
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun signInWithGoogle(userToken: String): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.signInWithGoogle(userToken)
            emitSource(result.asLiveData())
        }
    }

    fun loginWithEmail(email: String, password: String): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.loginWithEmail(email, password)
            emitSource(result.asLiveData())
        }
    }

    fun getSignInIntent() = dataRepository.getSignInIntent()
}