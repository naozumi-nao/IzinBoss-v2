package com.naozumi.izinboss.prototype_features

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result

interface IDataRepository {
    suspend fun signInWithGoogle(idToken: String): LiveData<Result<FirebaseUser>>
    suspend fun registerWithEmail(name: String, email: String, password: String): LiveData<Result<FirebaseUser>>
    suspend fun loginWithEmail(email: String, password: String): LiveData<Result<Unit>>
    suspend fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): LiveData<Result<Company>>
    suspend fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): LiveData<Result<Unit>>
    suspend fun getCompanyMembers(companyId: String?): LiveData<Result<List<User>>>
    suspend fun kickUserFromCompany(userId: String?): LiveData<Result<Unit>>
    suspend fun addLeaveRequestToDatabase(companyId: String, leaveRequest: LeaveRequest): LiveData<Result<Unit>>
    suspend fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>>
    suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean, managerName: String): LiveData<Result<Unit>>
    suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?): LiveData<Result<Unit>>
    suspend fun changeFullName(newName: String, user: User?): LiveData<Result<Unit>>
    suspend fun changeProfilePicture(file: Uri?): LiveData<Result<Unit>>
    suspend fun deleteAccount(userId: String?): LiveData<Result<Unit>>
    fun signOut()
    fun getSignInIntent(): Intent
    suspend fun convertFirebaseUserToUser(firebaseUser: FirebaseUser)
    fun getCurrentUserID(): String?
    suspend fun getUserData(userId: String?): User?
    suspend fun getCompanyData(companyId: String): Company?
    suspend fun saveUserToPreferences(user: User)
    suspend fun deleteCurrentUserFromPreferences()
}