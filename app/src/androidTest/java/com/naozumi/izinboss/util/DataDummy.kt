package com.naozumi.izinboss.util

import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User

object DataDummy {
    fun generateDummyLeaveRequests(): List<LeaveRequest> {
        val leaveRequestList = ArrayList<LeaveRequest>()
        val types = LeaveRequest.Type.values()
        val statuses = LeaveRequest.Status.values()

        for (i in 0 until 20) {
            val leaveRequest = LeaveRequest(
                id = "request_id_$i",
                companyId = "company_id_$i",
                employeeId = "employee$i",
                employeeName = "employee$i",
                createdAt = "creation_date_$i",
                startDate = "start_date_$i",
                endDate = "end_date_$i",
                reason = "reason_$i",
                type = types[i % types.size], // Cycles through available types
                status = statuses[i % statuses.size], // Cycles through available statuses
                reviewedBy = "reviewed_by_$i",
                reviewedOn = "reviewed_date_$i"
            )
            leaveRequestList.add(leaveRequest)
        }
        return leaveRequestList
    }

    fun generateDummyUsers(): List<User> {
        val userList = ArrayList<User>()
        val roleList = User.UserRole.values()

        for (i in 0 until 20) {
            val user = User(
                uid = "user_id_$i",
                name = "User Name $i",
                email = "user$i@gmail.com",
                profilePicture = "profile_picture_$i.jpg",
                companyId = "company_id_$i",
                role = roleList[i % roleList.size] // Cycles through available roles
            )
            userList.add(user)
        }
        return userList
    }

    fun generateDummyCompanies(): List<Company> {
        val companyList = ArrayList<Company>()
        val sectors = Company.IndustrySector.values()

        for (i in 0 until 20) {
            val company = Company(
                id = "company_id_$i",
                name = "Company Name $i",
                industrySector = sectors[i % sectors.size] // Cycles through available sectors
            )
            companyList.add(company)
        }
        return companyList
    }
}