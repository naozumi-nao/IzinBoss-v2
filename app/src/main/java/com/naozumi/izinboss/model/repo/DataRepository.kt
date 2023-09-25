package com.naozumi.izinboss.model.repo

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.wrapEspressoIdlingResource
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.NetworkDebounce
import com.naozumi.izinboss.model.util.ImageUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DataRepository (
    private val firebaseAuth: FirebaseAuth,
    private var googleSignInClient: GoogleSignInClient,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userPreferences: UserPreferences
    ) {
    // Mutex to make writes to cached values thread-safe.
    private val leaveRequestsMutex = Mutex()
    // Cache of leave requests.
    private var cachedLeaveRequests: List<LeaveRequest>? = null

    suspend fun signInWithGoogle(idToken: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun loginWithEmail(email: String, password: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun createCompany(companyName: String, industrySector: Company.IndustrySector?, user: User?): LiveData<Result<Company>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }


    suspend fun addUserToCompany(companyId: String, user: User?, position: User.UserRole?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun getCompanyMembers(companyId: String?): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun kickUserFromCompany(userId: String?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
                    saveUserToPreferences(user)

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
    }

    suspend fun addLeaveRequestToDatabase(companyId: String, leaveRequest: LeaveRequest): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun getAllLeaveRequests(companyId: String, refresh: Boolean = false): LiveData<Result<List<LeaveRequest>>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {

                if (refresh || cachedLeaveRequests == null) {
                    val leaveRequestList = mutableListOf<LeaveRequest>()
                    val leaveRequestCollection = firestore.collection("Companies").document(companyId).collection("Leave Requests")
                    val leaveRequestQuery = leaveRequestCollection.get().await()

                    if (leaveRequestQuery.isEmpty) {
                        leaveRequestsMutex.withLock { // If the collection is empty, return an empty list
                            cachedLeaveRequests = emptyList()
                        }
                    } else {
                        for (document in leaveRequestQuery) {
                            val leaveRequest = document.toObject(LeaveRequest::class.java)
                            leaveRequestList.add(leaveRequest)
                        }

                        leaveRequestsMutex.withLock {
                            cachedLeaveRequests = leaveRequestList
                        }
                    }
                }

                // Use cachedLeaveRequests with Mutex
                val leaveRequests = leaveRequestsMutex.withLock {
                    cachedLeaveRequests ?: emptyList()
                }

                emit(Result.Success(leaveRequests))
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
                        "status" to leaveRequest.status
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
    }

    suspend fun changeProfilePicture(file: Uri?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                if(file != null) {
                    val userId = getUserId()
                    if(userId != null) {
                        val storageReference = storage.reference
                            .child("profile_pictures")
                            .child("$userId.jpg")

                        // Delete the previous image (if it exists)
                        try {
                            storageReference.delete().await()
                        } catch (e: Exception) {
                            // Handle any errors during deletion (e.g., image doesn't exist)
                        }

                        val uploadTask = storageReference.putFile(file)
                        uploadTask.await()

                        val downloadUrl = storageReference.downloadUrl.await()
                        val newProfilePictureUrl = downloadUrl.toString()

                        val userRef = firestore.collection("Users").document(userId)
                        val userUpdate = mapOf(
                            "profilePicture" to newProfilePictureUrl
                        )
                        userRef.update(userUpdate).await()

                        emit(Result.Success(Unit))
                    }

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
    }

    // TODO: This deleteAccount deletes both account and company, use with caution. Need to separate it later
    suspend fun deleteAccount(userId: String?): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val firebaseUser = firebaseAuth.currentUser
                val databaseUser = getUserData(userId)

                if (firebaseUser != null && databaseUser != null && userId != null) {

                    val storageReference = storage.reference
                        .child("profile_pictures")
                        .child("$userId.jpg")

                    // Delete profile image (if it exists)
                    try {
                        storageReference.delete().await()
                    } catch (e: Exception) {
                        // Handle any errors during deletion (e.g., image doesn't exist)
                    }

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
    }

    fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    private suspend fun convertFirebaseUserToUser(firebaseUser: FirebaseUser) {
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

    fun getUserId(): String? {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.uid
    }

    suspend fun getUserData(userId: String?): User? {
        val userDocument = firestore.collection("Users").document(userId.toString()).get().await()
        if (userDocument.exists()) {
            return userDocument.toObject(User::class.java)
        }
        return null
    }

    suspend fun getCompanyData(companyId: String): Company? {
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
            storage: FirebaseStorage,
            userPreferences: UserPreferences
        ): DataRepository {
            return instance ?: synchronized(this) {
                instance ?: DataRepository(firebaseAuth, googleSignInClient, firestore, storage, userPreferences)
            }.also { instance = it }
        }
    }
}