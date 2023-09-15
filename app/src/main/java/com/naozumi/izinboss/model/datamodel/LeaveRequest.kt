package com.naozumi.izinboss.model.datamodel

data class LeaveRequest(
    var id: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val timeStamp: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val reason: String? = null,
    val type: Type = Type.SICK,
    val status: Status = Status.PENDING
) {
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
