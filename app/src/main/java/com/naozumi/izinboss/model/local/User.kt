package com.naozumi.izinboss.model.local

data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val companyCode: String? = null,
    val isManager: Boolean = false
)
