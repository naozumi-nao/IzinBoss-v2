package com.naozumi.izinboss.model.local

data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val profilePicture: String? = null,
    val role: UserRole? = null
) {
    enum class UserRole {
        MANAGER,
        EMPLOYEE
    }
}
