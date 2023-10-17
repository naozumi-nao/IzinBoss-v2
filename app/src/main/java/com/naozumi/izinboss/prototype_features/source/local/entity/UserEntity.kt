package com.naozumi.izinboss.prototype_features.source.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val uid: String,
    var name: String,
    val email: String,
    val profilePicture: String,
    var companyId: String,
    var role: UserRole
)  : Parcelable {
    enum class UserRole {
        MANAGER,
        EMPLOYEE
    }
}