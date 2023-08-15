package com.naozumi.izinboss.model.local

data class Leave(
    val leaveID: String? = null,
    val timeStamp: Long? = 0,
    val title: String? = null,
    val staffName: String? = null,
    val staffID: String? = null,
    val companyID: String? = null,
    val description: String? = null,
    val startDuration: String? = null,
    val finishDuration: String? = null,
    val note: String? = null,
    val status: String? = null
)
