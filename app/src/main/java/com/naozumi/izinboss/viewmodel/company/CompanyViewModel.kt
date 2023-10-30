package com.naozumi.izinboss.viewmodel.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CompanyViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): LiveData<Result<Company>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.createCompany(companyName, industrySector, user)
            emitSource(result.asLiveData())
        }
    }

    fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.addUserToCompany(companyId, user, position)
            emitSource(result.asLiveData())
        }
    }


    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }

    fun kickUserFromCompany(userId: String?): LiveData<Result<Unit>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.removeUserFromCompany(userId)
            emitSource(result.asLiveData())
        }
    }
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    fun getCompanyData(companyId: String): LiveData<Result<Company>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.getCompanyData(companyId)
            emitSource(result.asLiveData())
        }
    }
    fun getCompanyMembers(companyId: String?): LiveData<Result<List<User>>> {
        return liveData(Dispatchers.Main) {
            val result = dataRepository.getCompanyMembers(companyId)
            emitSource(result.asLiveData())
        }
    }
}