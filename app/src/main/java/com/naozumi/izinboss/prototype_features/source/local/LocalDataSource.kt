package com.naozumi.izinboss.prototype_features.source.local

import com.naozumi.izinboss.prototype_features.source.local.entity.CompanyEntity
import com.naozumi.izinboss.prototype_features.source.local.entity.LeaveRequestEntity
import com.naozumi.izinboss.prototype_features.source.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSource private constructor(
    private val userDao: UserDao,
    private val companyDao: CompanyDao,
    private val leaveRequestDao: LeaveRequestDao
) {
    // ---UserEntity---
    fun getUser(userId: String): Flow<UserEntity> = userDao.getUser(userId)
    fun getUserWithSameCompany(companyId: String): Flow<List<UserEntity>> =
        userDao.getUsersWithSameCompany(companyId)
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    // ---CompanyEntity---
    fun getCompany(companyId: String): Flow<CompanyEntity> =
        companyDao.getCompany(companyId)
    suspend fun insertCompany(company: CompanyEntity) =
        companyDao.insertCompany(company)
    suspend fun updateCompany(company: CompanyEntity) =
        companyDao.updateCompany(company)
    suspend fun deleteCompany(company: CompanyEntity) =
        companyDao.deleteCompany(company)

    // ---Leave Request---
    fun getAllLeaveRequests(companyId: String): Flow<List<LeaveRequestEntity>> =
        leaveRequestDao.getAllLeaveRequests(companyId)
    fun getLeaveRequest(id: String): Flow<LeaveRequestEntity> =
        leaveRequestDao.getLeaveRequest(id)
    suspend fun insertLeaveRequest(leaveRequest: LeaveRequestEntity) =
        leaveRequestDao.insertLeaveRequest(leaveRequest)
    suspend fun insertLeaveRequestList(leaveRequestList: List<LeaveRequestEntity>) =
        leaveRequestDao.insertLeaveRequestList(leaveRequestList)
    suspend fun updateLeaveRequest(leaveRequest: LeaveRequestEntity) =
        leaveRequestDao.updateLeaveRequest(leaveRequest)
    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequestEntity) =
        leaveRequestDao.deleteLeaveRequest(leaveRequest)


    companion object {
        private var instance: LocalDataSource? = null

        fun getInstance(
            userDao: UserDao,
            companyDao: CompanyDao,
            leaveRequestDao: LeaveRequestDao
        ): LocalDataSource =
            instance ?: synchronized(this) {
                instance ?: LocalDataSource(userDao, companyDao, leaveRequestDao)
            }
    }
}