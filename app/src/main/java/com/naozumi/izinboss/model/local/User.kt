package com.naozumi.izinboss.model.local

data class User(
    val userID: String? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val companyCode: String? = null,
    val isEmployee: String? = null
)
