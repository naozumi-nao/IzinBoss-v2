package com.naozumi.izinboss.viewmodel.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.naozumi.izinboss.model.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.naozumi.izinboss.model.helper.Result

class RegisterViewModel(private val dataRepository: DataRepository) : ViewModel() {
    fun registerWithEmail(name: String, email: String, password: String): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.registerWithEmail(name, email, password)
            emitSource(result.asLiveData())
        }
    }
}