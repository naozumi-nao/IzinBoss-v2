package com.naozumi.izinboss.model.repo

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.wrapIdlingResource
import com.naozumi.izinboss.model.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DataRepository (
    private val firebaseAuth: FirebaseAuth,
    private var googleSignInClient: GoogleSignInClient,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
    ): CompanyRepository, LeaveRequestRepository, UserRepository {

    override fun signInWithGoogle(idToken: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val user = authResult.user
                if (user != null) {
                    //user.sendEmailVerification()
                    convertFirebaseUserToUser(user)
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("Sign-in result does not contain user data"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun registerWithEmail(name: String, email: String, password: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = firebaseAuth.currentUser
                if (user != null) {
                    user.updateProfile(
                        userProfileChangeRequest { displayName = name }
                    ).await()
                    //user.sendEmailVerification()
                    convertFirebaseUserToUser(user)
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("Sign-in result does not contain user data"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun loginWithEmail(email: String, password: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = getUserData(getCurrentUserId())
                if (user != null) {
                    saveUserToPreferences(user)
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("Sign-in result does not contain user data"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): Flow<Result<Company>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                val companyId = firestore.collection("Companies").document().id // Generate a unique ID
                if (user != null) {
                    // Update user's role and companyId locally
                    user.role = User.UserRole.MANAGER
                    user.companyId = companyId
                    user.companyName = companyName

                    val company = Company(
                        companyId,
                        companyName,
                        industrySector,
                        memberCount = 1
                    )

                    val companyCollection = firestore.collection("Companies")
                    companyCollection.document(companyId).set(company).await() // Create the document with the specified ID

                    val userDocumentRef = firestore.collection("Users").document(user.uid.toString())
                    val userUpdate = mapOf(
                        "companyId" to companyId,
                        "companyName" to companyName,
                        "role" to user.role
                    )
                    userDocumentRef.update(userUpdate).await()
                    saveUserToPreferences(user)

                    emit(Result.Success(company))
                } else {
                    emit(Result.Error("User is Null!"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getCompanyData(companyId: String): Flow<Result<Company>> = flow {
        try {
            if(companyId.isNotBlank()) {
                val companyDocument =
                    firestore.collection("Companies").document(companyId).get().await()
                if (companyDocument.exists()) {
                    val company = companyDocument.toObject(Company::class.java)
                    if (company != null) {
                        emit(Result.Success(company))
                    } else {
                        emit(Result.Error("Company is Null!"))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    override fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                if (user != null) {
                    // Check if the company with the specified companyId exists
                    val companyDocumentRef = firestore.collection("Companies").document(companyId)
                    val companyDocument = companyDocumentRef.get().await()
                    if (companyDocument.exists()) {
                        val company = companyDocument.toObject(Company::class.java)
                        val userDocumentRef = firestore.collection("Users").document(user.uid.toString())

                        user.role = when (position) {
                            User.UserRole.MANAGER -> User.UserRole.MANAGER
                            User.UserRole.EMPLOYEE -> User.UserRole.EMPLOYEE
                            else -> User.UserRole.EMPLOYEE
                        }
                        //Update User in Firestore
                        val userUpdate = mapOf(
                            "companyId" to companyId,
                            "companyName" to company?.name,
                            "role" to user.role
                        )
                        userDocumentRef.update(userUpdate).await()

                        if (company != null) {
                            val companyUpdate = mapOf(
                                "memberCount" to company.memberCount + 1
                            )
                            companyDocumentRef.update(companyUpdate).await()
                        }

                        emit(Result.Success(Unit))
                    } else {
                        emit(Result.Error("Company with ID $companyId does not exist"))
                    }
                } else {
                    emit(Result.Error("User is Null"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun joinCompany(companyId: String, user: User?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                if (user != null) {
                    // Check if the company with the specified companyId exists
                    val companyDocumentRef = firestore.collection("Companies").document(companyId)
                    val companyDocument = companyDocumentRef.get().await()
                    if (companyDocument.exists()) {
                        val company = companyDocument.toObject(Company::class.java)
                        val userDocumentRef = firestore.collection("Users").document(user.uid.toString())

                        //Update User Locally
                        user.role = User.UserRole.EMPLOYEE
                        user.companyId = companyId
                        user.companyName = company?.name
                        saveUserToPreferences(user)

                        //Update User in Firestore
                        val userUpdate = mapOf(
                            "companyId" to companyId,
                            "companyName" to company?.name,
                            "role" to user.role
                        )
                        userDocumentRef.update(userUpdate).await()

                        if (company != null) {
                            val companyUpdate = mapOf(
                                "memberCount" to company.memberCount + 1
                            )
                            companyDocumentRef.update(companyUpdate).await()
                        }

                        emit(Result.Success(Unit))
                    } else {
                        emit(Result.Error("Company with ID $companyId does not exist"))
                    }
                } else {
                    emit(Result.Error("User is Null"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getCompanyMembers(companyId: String?): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                val companyMembersList = mutableListOf<User>()
                val usersCollection = firestore.collection("Users")
                val query = usersCollection.whereEqualTo("companyId", companyId).get().await()

                for (document in query) {
                    val companyMember = document.toObject(User::class.java)
                    companyMembersList.add(companyMember)
                }

                emit(Result.Success(companyMembersList))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun removeUserFromCompany(userId: String?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                val user = getUserData(userId)
                if (user != null) {
                    val companyDocumentRef = firestore.collection("Companies").document(user.companyId.toString())
                    val companyDocument = companyDocumentRef.get().await()
                    val company = companyDocument.toObject(Company::class.java)

                    if (company != null) {
                        if(company.memberCount == 1) {
                            companyDocumentRef.delete().await()
                        } else {
                            val companyUpdate = mapOf(
                                "memberCount" to company.memberCount - 1
                            )
                            companyDocumentRef.update(companyUpdate).await()
                        }
                    }

                    val userDocumentRef = firestore.collection("Users").document(user.uid.toString())

                    user.companyId = null
                    user.companyName = null
                    user.role = null

                    val userUpdate = mapOf(
                        "companyId" to null,
                        "companyName" to null,
                        "role" to null
                    )
                    userDocumentRef.update(userUpdate).await()
                    if (user.uid == getCurrentUserId()) {
                        saveUserToPreferences(user)
                    }

                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("User is Null"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun addLeaveRequest(companyId: String, leaveRequest: LeaveRequest): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                val leaveRequestCollection = firestore.collection("Companies").document(companyId).collection("Leave Requests")
                val leaveRequestId = leaveRequestCollection.document().id // Generate a unique ID
                leaveRequest.id = leaveRequestId
                leaveRequest.companyId = companyId

                leaveRequestCollection.document(leaveRequestId).set(leaveRequest).await() // Create the document with the specified ID

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    // val filteredLeaveData = leaveData.filter { it.employeeId == user?.uid }
    override fun getAllLeaveRequests(user: User?): Flow<Result<List<LeaveRequest>>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                if (user != null) {
                    val leaveRequestList = mutableListOf<LeaveRequest>()
                    val leaveRequestCollection = firestore.collection("Companies")
                        .document(user.companyId.toString())
                        .collection("Leave Requests")

                    val leaveRequestQuery = if (user.role == User.UserRole.MANAGER) {
                        leaveRequestCollection.get().await()
                    } else {
                        leaveRequestCollection.whereEqualTo("employeeId", user.uid).get().await()
                    }

                    for (document in leaveRequestQuery) {
                        val leaveRequest = document.toObject(LeaveRequest::class.java)

                        leaveRequestList.add(leaveRequest)
                    }
                    emit(Result.Success(leaveRequestList))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean, managerName: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
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
                    emit(Result.Error("Leave Request is Null"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun deleteLeaveRequest(leaveRequest: LeaveRequest?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
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
                    emit(Result.Error("Leave Request is null"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun changeFullName(newName: String, user: User?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
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
                    emit(Result.Error("User is Null"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    // TODO: This deleteAccount deletes both account and company, use with caution. Need to separate it later
    override fun deleteAccount(userId: String?): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        wrapIdlingResource {
            try {
                val firebaseUser = firebaseAuth.currentUser
                val databaseUser = getUserData(userId)

                if (firebaseUser != null && databaseUser != null && userId != null) {
                    firestore.collection("Users").document(userId).delete().await()

                    val companyId = databaseUser.companyId.toString()
                    val companyDocumentRef = firestore.collection("Companies")
                        .document(companyId)
                    val companyDocument = companyDocumentRef.get().await()
                    val company = companyDocument.toObject(Company::class.java)

                    if (company != null) {
                        if(company.memberCount == 1) {
                            companyDocumentRef.delete().await()
                        } else {
                            val companyUpdate = mapOf(
                                "memberCount" to company.memberCount - 1
                            )
                            companyDocumentRef.update(companyUpdate).await()
                        }
                    }

                    firebaseUser.delete().await()
                    signOut()
                    deleteCurrentUserFromPreferences()

                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("User Not Found"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

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

    override fun getCurrentUserId(): String {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.uid.toString()
    }

    override suspend fun getUserData(userId: String?): User? {
        val userDocument = withContext(Dispatchers.IO) {
                firestore.collection("Users").document(userId.toString()).get().await()
            }
        if (userDocument.exists()) {
            return userDocument.toObject(User::class.java)
        }
        return null
    }

    suspend fun saveUserToPreferences(user: User) {
        userPreferences.saveUser(user)
    }

    suspend fun getUser(): User? {
        return userPreferences.getUser().first()
    }

    override fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }

    suspend fun deleteCurrentUserFromPreferences() {
        userPreferences.deleteCurrentUserPref()
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