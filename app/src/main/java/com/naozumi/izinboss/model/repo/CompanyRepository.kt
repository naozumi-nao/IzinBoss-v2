package com.naozumi.izinboss.model.repo

import androidx.lifecycle.LiveData
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): Flow<Result<Company>>
    fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): Flow<Result<Unit>>
    fun getCompanyMembers(companyId: String?): Flow<Result<List<User>>>
    fun removeUserFromCompany(userId: String?): Flow<Result<Unit>>
    fun getCompanyData(companyId: String): Flow<Result<Company>>
    fun joinCompany(companyId: String, user: User?): Flow<Result<Unit>>
}
