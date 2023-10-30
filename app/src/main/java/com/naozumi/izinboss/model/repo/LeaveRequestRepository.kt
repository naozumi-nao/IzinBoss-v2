package com.naozumi.izinboss.model.repo

import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import kotlinx.coroutines.flow.Flow

interface LeaveRequestRepository {
    fun addLeaveRequest(companyId: String, leaveRequest: LeaveRequest): Flow<Result<Unit>>
    fun getAllLeaveRequests(user: User?): Flow<Result<List<LeaveRequest>>>
    fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean, managerName: String): Flow<Result<Unit>>
    fun deleteLeaveRequest(leaveRequest: LeaveRequest?): Flow<Result<Unit>>
}
