package com.naozumi.izinboss.viewmodel.company

import androidx.lifecycle.ViewModel
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences
import kotlinx.coroutines.flow.Flow

class CompanyViewModel(private val dataRepository: DataRepository, private val userPreferences: UserPreferences): ViewModel() {
    suspend fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?) =
        dataRepository.createCompany(companyName, industrySector, user)

    suspend fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?) =
        dataRepository.addUserToCompany(companyId, user, position)

    fun getUser(): Flow<User?> {
        return userPreferences.getUser()
    }

    suspend fun kickUserFromCompany(userId: String?) = dataRepository.kickUserFromCompany(userId)
    suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest, isApproved: Boolean, managerName: String) =
        dataRepository.changeLeaveRequestStatus(leaveRequest, isApproved, managerName)
    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?) = dataRepository.deleteLeaveRequest(leaveRequest)
    suspend fun getUserData(userId: String) = dataRepository.getUserData(userId)
    suspend fun getCompanyData(companyId: String) = dataRepository.getCompanyData(companyId)
    suspend fun getCompanyMembers(companyId: String?) =
        dataRepository.getCompanyMembers(companyId)

}