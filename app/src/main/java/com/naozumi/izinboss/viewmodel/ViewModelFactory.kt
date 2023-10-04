package com.naozumi.izinboss.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naozumi.izinboss.model.di.Injection
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.entry.LoginViewModel
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import com.naozumi.izinboss.viewmodel.user.UserProfileViewModel

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
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideDataStore(context))
            }.also { instance = it }
    }

    /*
        companion object {
            @Volatile
            private var INSTANCE: ClassSingleton? = null
            private val lock = Any()
            val instance: ClassSingleton?
                get() {
                    if (INSTANCE == null) {
                        synchronized(lock) {
                            if (INSTANCE == null) {
                                INSTANCE = ClassSingleton()
                            }
                        }
                    }
                    return INSTANCE
                } // getters and setters
        }
    }
     */
}