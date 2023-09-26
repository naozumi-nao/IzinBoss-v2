package com.naozumi.izinboss.model.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// TODO: Add Reviewed By: Manager name

@Parcelize
data class LeaveRequest(
    var id: String? = null,
    var companyId: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val timeStamp: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val reason: String? = null,
    val type: Type? = null,
    var status: Status = Status.PENDING
): Parcelable {
    enum class Type {
        SICK,
        VACATION,
        PERSONAL
    }
    enum class Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
