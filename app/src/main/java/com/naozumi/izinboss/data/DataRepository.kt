package com.naozumi.izinboss.data

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.naozumi.izinboss.model.local.Company
import com.naozumi.izinboss.model.local.LeaveRequest
import com.naozumi.izinboss.model.local.User
import kotlinx.coroutines.tasks.await

class DataRepository (
    private val firebaseAuth: FirebaseAuth,
    private var googleSignInClient: GoogleSignInClient,
    private val databaseReference: DatabaseReference
    ) {

    suspend fun signInWithGoogle(idToken: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                //user.sendEmailVerification()
                convertFirebaseUserToUser(user)
                emit(Result.Success(user))
            } else {
                emit(Result.Error("Sign-in result does not contain user data"))
            }
        } catch (e: FirebaseAuthException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.updateProfile(
                    userProfileChangeRequest { displayName = name }
                ).await()
                //user.sendEmailVerification()
                convertFirebaseUserToUser(user)
                emit(Result.Success(user))
            } else {
                emit(Result.Error("Sign-in result does not contain user data"))
            }
        } catch (e: FirebaseAuthException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun loginWithEmail(email: String, password: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = firebaseAuth.currentUser
            if (user != null) {
                emit(Result.Success(user))
            } else {
                emit(Result.Error("Sign-in result does not contain user data"))
            }
        } catch (e: FirebaseAuthException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>> = liveData {
        emit(Result.Loading)
        try {
            val dataSnapShot = databaseReference.child("Companies").child(companyId).child("leaveRequestList").get().await()
            val leaveRequestList = mutableListOf<LeaveRequest>()

            dataSnapShot.children.forEach { leaveSnapShot ->
                val leaveRequest = leaveSnapShot.getValue(LeaveRequest::class.java)
                leaveRequest?.let {
                    leaveRequestList.add(it)
                }
            }
            emit(Result.Success(leaveRequestList))
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: DatabaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun addLeaveRequestToDatabase(companyId: String, leaveRequest: LeaveRequest): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            val companyRef = databaseReference.child("Companies").child(companyId)
            val leaveRequestListRef = companyRef.child("leaveRequestList")
            val leaveID = leaveRequestListRef.push().key

            if (leaveID != null) {
                leaveRequest.id = leaveID
                leaveRequestListRef.child(leaveID).setValue(leaveRequest).await()
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error("Failed to generate leave ID"))
            }
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: DatabaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun createCompany(companyName: String, userId: String): LiveData<Result<Company>> = liveData {
        emit(Result.Loading)
        try {
            val companyId = databaseReference.child("Companies").push().key

            if (companyId != null) {
                val user = getUserData(userId) // Fetch user data from repository

                if (user != null) {
                    // Update user locally
                    user.role = User.UserRole.MANAGER
                    user.companyId = companyId

                    val company = Company(
                        id = companyId,
                        name = companyName,
                        members = listOf(user)
                    )
                    // Save Company in the Firebase database
                    databaseReference.child("Companies").child(companyId).setValue(company).await()
                    // Update user in the Firebase database
                    databaseReference.child("Users").child(userId).setValue(user).await()

                    emit(Result.Success(company))
                } else {
                    emit(Result.Error("User data not found"))
                }
            } else {
                emit(Result.Error("Failed to generate company ID"))
            }
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: DatabaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun addEmployee(companyId: String, employee: User): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            val companyRef = databaseReference.child("Companies").child(companyId)
            val existingMembersSnapshot = companyRef.child("members").get().await()
            val existingMembers = existingMembersSnapshot.getValue(object : GenericTypeIndicator<List<User>>() {})

            if (existingMembers != null) {
                val updatedMembers = existingMembers.toMutableList()
                updatedMembers.add(employee)
                companyRef.child("members").setValue(updatedMembers).await()

                // Initialize an empty leaveRequestList for the company
                val leaveRequestList = mutableListOf<LeaveRequest>()
                companyRef.child("leaveRequestList").setValue(leaveRequestList).await()

                emit(Result.Success(Unit))
            } else {
                emit(Result.Error("Failed to update company members"))
            }
        } catch (e: FirebaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: DatabaseException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    private fun convertFirebaseUserToUser(firebaseUser: FirebaseUser) {
        val user = firebaseUser.let {
            User(
                uid = it.uid,
                name = it.displayName,
                email = it.email,
                profilePicture = it.photoUrl.toString(),
            )
        }

        // "uid" from FirebaseUser is directly used as the key for each user document
        databaseReference.child("Users").child(firebaseUser.uid).setValue(user)
    }

    fun getCurrentUser(): String? {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.uid
    }

    suspend fun getUserData(userId: String): User? {
        val dataSnapshot = databaseReference.child("Users").child(userId).get().await()
        return dataSnapshot.getValue(User::class.java)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(firebaseAuth: FirebaseAuth, googleSignInClient: GoogleSignInClient, databaseReference: DatabaseReference): DataRepository {
            return instance ?: synchronized(this) {
                instance ?: DataRepository(firebaseAuth, googleSignInClient, databaseReference)
            }.also { instance = it }
        }
    }
}