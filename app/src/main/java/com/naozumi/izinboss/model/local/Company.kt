package com.naozumi.izinboss.model.local

data class Company (
    val id: String? = null,
    val name: String? = null,
    val members: List<User>? = null,
    val leaveRequestList: List<LeaveRequest>? = null
)