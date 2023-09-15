package com.naozumi.izinboss.model.datamodel

data class Company (
    val id: String? = null,
    val name: String? = null,
    val members: List<User>? = emptyList(),
    val leaveRequestList: List<LeaveRequest>? = emptyList()
)