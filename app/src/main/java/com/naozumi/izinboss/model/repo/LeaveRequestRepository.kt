package com.naozumi.izinboss.model.repo

import androidx.lifecycle.LiveData
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.helper.Result

interface LeaveRequestRepository {
    suspend fun addLeaveRequest(companyId: String, leaveRequest: LeaveRequest): LiveData<Result<Unit>>
    suspend fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>>
    suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean, managerName: String): LiveData<Result<Unit>>
    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?): LiveData<Result<Unit>>
}
