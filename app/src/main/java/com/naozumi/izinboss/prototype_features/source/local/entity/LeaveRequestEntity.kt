package com.naozumi.izinboss.prototype_features.source.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class LeaveRequestEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var companyId: String,
    val employeeId: String,
    val employeeName: String,
    val createdAt: String,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val type: Type,
    var status: Status,
    var reviewedBy: String,
    var reviewedOn: String
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
