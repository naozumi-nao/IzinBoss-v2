package com.naozumi.izinboss.prototype_features.source.local

import androidx.room.*
import com.naozumi.izinboss.prototype_features.source.local.entity.LeaveRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveRequestDao {
    @Query("SELECT * FROM LeaveRequestEntity WHERE companyId = :companyId")
    fun getAllLeaveRequests(companyId: String): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM LeaveRequestEntity WHERE id = :id")
    fun getLeaveRequest(id: String): Flow<LeaveRequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(leaveRequest: LeaveRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequestList(leaveRequest: List<LeaveRequestEntity>)

    @Update
    suspend fun updateLeaveRequest(leaveRequest: LeaveRequestEntity)

    @Delete
    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequestEntity)
}