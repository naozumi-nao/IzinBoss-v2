package com.naozumi.izinboss.model.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String? = null,
    var name: String? = null,
    val email: String? = null,
    val profilePicture: String? = null,
    var companyId: String? = null,
    var role: UserRole? = null
)  : Parcelable {
    enum class UserRole {
        MANAGER,
        EMPLOYEE
    }
}
