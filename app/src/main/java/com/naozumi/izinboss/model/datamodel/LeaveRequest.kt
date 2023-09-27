package com.naozumi.izinboss.model.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LeaveRequest(
    var id: String? = null,
    var companyId: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val createdAt: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val reason: String? = null,
    val type: Type? = null,
    var status: Status = Status.PENDING,
    var reviewedBy: String? = null,
    var reviewedOn: String? = null
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
