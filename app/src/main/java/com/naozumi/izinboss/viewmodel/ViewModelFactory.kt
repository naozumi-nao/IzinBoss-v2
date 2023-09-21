package com.naozumi.izinboss.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.model.di.Injection
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.entry.LoginViewModel
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel

class ViewModelFactory private constructor(
    private val dataRepository: DataRepository,
    private val userPreferences: UserPreferences
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dataRepository, userPreferences) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(dataRepository) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(dataRepository) as T
        }
        if (modelClass.isAssignableFrom(AddLeaveViewModel::class.java)) {
            return AddLeaveViewModel(dataRepository, userPreferences) as T
        }
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(dataRepository, userPreferences) as T
        }
        if (modelClass.isAssignableFrom(CompanyViewModel::class.java)) {
            return CompanyViewModel(dataRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context), Injection.provideDataStore(context))
            }.also { instance = it }
    }
}