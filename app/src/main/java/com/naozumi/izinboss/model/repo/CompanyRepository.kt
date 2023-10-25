package com.naozumi.izinboss.model.repo

import androidx.lifecycle.LiveData
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result

interface CompanyRepository {
    suspend fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): LiveData<Result<Company>>
    suspend fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): LiveData<Result<Unit>>
    suspend fun getCompanyMembers(companyId: String?): LiveData<Result<List<User>>>
    suspend fun removeUserFromCompany(userId: String?): LiveData<Result<Unit>>
    suspend fun getCompanyData(companyId: String): Company?
}
