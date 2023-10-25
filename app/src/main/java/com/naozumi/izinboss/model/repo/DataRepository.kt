package com.naozumi.izinboss.model.repo

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.TimeUtils
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DataRepository (
    private val firebaseAuth: FirebaseAuth,
    private var googleSignInClient: GoogleSignInClient,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
    ): CompanyRepository, LeaveRequestRepository, UserRepository {

    override suspend fun signInWithGoogle(idToken: String): LiveData<Result<FirebaseUser>> = liveData {
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

    override suspend fun registerWithEmail(name: String, email: String, password: String): LiveData<Result<FirebaseUser>> = liveData {
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

    override suspend fun loginWithEmail(email: String, password: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = getUserData(firebaseAuth.currentUser?.uid.toString())
                if (user != null) {
                    saveUserToPreferences(user)
                    emit(Result.Success(Unit))
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

    override suspend fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): LiveData<Result<Company>> = liveData {
        emit(Result.Loading)

            try {
                val companyId = firestore.collection("Companies").document().id // Generate a unique ID
                if (user != null) {
                    // Update user's role and companyId locally
                    user.role = User.UserRole.MANAGER
                    user.companyId = companyId

                    val company = Company(
                        companyId,
                        companyName,
                        industrySector
                    )

                    val companyCollection = firestore.collection("Companies")
                    companyCollection.document(companyId).set(company).await() // Create the document with the specified ID

                    val userDocumentRef = firestore.collection("Users").document(user.uid.toString())
                    val userUpdate = mapOf(
                        "companyId" to companyId,
                        "role" to user.role
                    )
                    userDocumentRef.update(userUpdate).await()
                    saveUserToPreferences(user)

                    emit(Result.Success(company))
                } else {
                    emit(Result.Error("Error: User is Null!"))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }


    override suspend fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                if (user != null) {
                    // Check if the company with the specified companyId exists
                    val companyDocument = firestore.collection("Companies").document(companyId).get().await()
                    if (companyDocument.exists()) {
                        val userDocumentRef = firestore.collection("Users").document(user.uid.toString())

                        //Update User Locally
                        user.role = when (position) {
                            User.UserRole.MANAGER -> User.UserRole.MANAGER
                            User.UserRole.EMPLOYEE -> User.UserRole.EMPLOYEE
                            else -> User.UserRole.EMPLOYEE
                        }
                        user.companyId = companyId

                        val userUpdate = mapOf(
                            "companyId" to companyId,
                            "role" to user.role
                        )
                        userDocumentRef.update(userUpdate).await()
                        saveUserToPreferences(user)

                        emit(Result.Success(Unit))
                    } else {
                        emit(Result.Error("Error: Company with ID $companyId does not exist"))
                    }
                } else {
                    emit(Result.Error("Error: User is Null"))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun getCompanyMembers(companyId: String?): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)

            try {
                val companyMembersList = mutableListOf<User>()
                val usersCollection = firestore.collection("Users")
                val query = usersCollection.whereEqualTo("companyId", companyId).get().await()

                for (document in query) {
                    val companyMember = document.toObject(User::class.java)
                    companyMembersList.add(companyMember)
                }

                emit(Result.Success(companyMembersList))
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun removeUserFromCompany(userId: String?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                val user = getUserData(userId)
                if (user != null) {
                    val userDocumentRef = firestore.collection("Users").document(user.uid.toString())

                    user.companyId = null
                    user.role = null

                    val userUpdate = mapOf(
                        "companyId" to null,
                        "role" to null
                    )
                    userDocumentRef.update(userUpdate).await()
                    if (user.uid == getCurrentUserID()) {
                        saveUserToPreferences(user)
                    }

                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("Error: User is Null"))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun addLeaveRequestToDatabase(companyId: String, leaveRequest: LeaveRequest): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                val leaveRequestCollection = firestore.collection("Companies").document(companyId).collection("Leave Requests")
                val leaveRequestId = leaveRequestCollection.document().id // Generate a unique ID
                leaveRequest.id = leaveRequestId
                leaveRequest.companyId = companyId

                leaveRequestCollection.document(leaveRequestId).set(leaveRequest).await() // Create the document with the specified ID

                emit(Result.Success(Unit))

            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>> = liveData {
        emit(Result.Loading)

            try {
                val leaveRequestList = mutableListOf<LeaveRequest>()
                val leaveRequestCollection = firestore.collection("Companies").document(companyId).collection("Leave Requests")
                val leaveRequestQuery = leaveRequestCollection.get().await()

                if (leaveRequestQuery.isEmpty) {
                    // If the collection is empty, return an empty list
                    emit(Result.Success(emptyList()))
                } else {
                    for (document in leaveRequestQuery) {
                        val leaveRequest = document.toObject(LeaveRequest::class.java)
                        leaveRequestList.add(leaveRequest)
                    }
                    emit(Result.Success(leaveRequestList))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean, managerName: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                if(leaveRequest != null) {
                    val leaveRequestCollection =
                        firestore.collection("Companies")
                            .document(leaveRequest.companyId.toString())
                            .collection("Leave Requests")

                    if(isApproved) {
                        leaveRequest.status = LeaveRequest.Status.APPROVED
                    } else {
                        leaveRequest.status = LeaveRequest.Status.REJECTED
                    }

                    val leaveRequestUpdate = mapOf(
                        "status" to leaveRequest.status,
                        "reviewedBy" to managerName,
                        "reviewedOn" to TimeUtils.getCurrentDateAndTime()
                    )

                    leaveRequestCollection.document(leaveRequest.id.toString()).update(leaveRequestUpdate).await()

                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("ERROR: Leave Request is Null"))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                if(leaveRequest != null) {
                    val leaveRequestRef =
                        firestore.collection("Companies")
                            .document(leaveRequest.companyId.toString())
                            .collection("Leave Requests")
                            .document(leaveRequest.id.toString())
                    leaveRequestRef.delete().await()
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("ERROR: Leave Request is null"))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override suspend fun changeFullName(newName: String, user: User?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                if(user != null) {
                    val userDocumentRef = firestore.collection("Users").document(user.uid.toString())

                    user.name = newName

                    val userUpdate = mapOf(
                        "name" to user.name
                    )
                    userDocumentRef.update(userUpdate).await()
                    saveUserToPreferences(user)

                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("ERROR: User is Null"))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: StorageException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    // TODO: This deleteAccount deletes both account and company, use with caution. Need to separate it later
    override suspend fun deleteAccount(userId: String?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

            try {
                val firebaseUser = firebaseAuth.currentUser
                val databaseUser = getUserData(userId)

                if (firebaseUser != null && databaseUser != null && userId != null) {
                    firestore.collection("Users").document(userId).delete().await()

                    val companyId = databaseUser.companyId.toString()
                    val companyRef = firestore.collection("Companies").document(companyId)
                    companyRef.delete().await()

                    firebaseUser.delete().await()
                    signOut()
                    deleteCurrentUserFromPreferences()

                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("Error: User Not Found"))
                }
            } catch (e: FirebaseAuthException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: StorageException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }

    override fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }

    override fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override suspend fun convertFirebaseUserToUser(firebaseUser: FirebaseUser) {
        val userRef = firestore.collection("Users").document(firebaseUser.uid)

        val documentSnapshot = suspendCoroutine { continuation ->
            userRef.get().addOnSuccessListener { documentSnapshot ->
                continuation.resume(documentSnapshot)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }

        if (documentSnapshot == null || !documentSnapshot.exists()) {
            // User doesn't exist, create a new user document
            val newUser = User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName,
                email = firebaseUser.email,
                profilePicture = firebaseUser.photoUrl.toString()
            )
            userRef.set(newUser).await()
            saveUserToPreferences(newUser)
        } else {
            val user = getUserData(firebaseUser.uid)
            if (user != null) {
                saveUserToPreferences(user)
            }
        }
    }

    override fun getCurrentUserID(): String? {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.uid
    }

    override suspend fun getUserData(userId: String?): User? {
        val userDocument = firestore.collection("Users").document(userId.toString()).get().await()
        if (userDocument.exists()) {
            return userDocument.toObject(User::class.java)
        }
        return null
    }

    override suspend fun getCompanyData(companyId: String): Company? {
        if(companyId.isNotBlank()) {
            val companyDocument = firestore.collection("Companies").document(companyId).get().await()
            if (companyDocument.exists()) {
                return companyDocument.toObject(Company::class.java)
            }
        }
        return null
    }

    private suspend fun saveUserToPreferences(user: User) {
        userPreferences.saveUser(user)
    }

    private suspend fun deleteCurrentUserFromPreferences() {
        userPreferences.deleteCurrentUserDataStore()
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(
            firebaseAuth: FirebaseAuth,
            googleSignInClient: GoogleSignInClient,
            firestore: FirebaseFirestore,
            userPreferences: UserPreferences
        ): DataRepository {
            return instance ?: synchronized(this) {
                instance ?: DataRepository(firebaseAuth, googleSignInClient, firestore, userPreferences)
            }.also { instance = it }
        }
    }
}