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
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.wrapEspressoIdlingResource
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class DataRepository (
    private val firebaseAuth: FirebaseAuth,
    private var googleSignInClient: GoogleSignInClient,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
    ) {

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

    suspend fun loginWithEmail(email: String, password: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
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
    }

    suspend fun createCompany(companyName: String, userId: String): LiveData<Result<Company>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val user = getUserData(userId)
                if (user != null) {
                    user.role = User.UserRole.MANAGER

                    val companyId = firestore.collection("Companies").document().id // Generate a unique ID

                    val company = Company(
                        id = companyId,
                        name = companyName,
                        members = mutableListOf(user)
                    )

                    val companyCollection = firestore.collection("Companies")
                    companyCollection.document(companyId).set(company).await() // Create the document with the specified ID

                    val userDocumentRef = firestore.collection("Users").document(userId)
                    userDocumentRef.update("companyId", companyId).await()

                    emit(Result.Success(company))
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
                val companyDocumentRef = firestore.collection("Companies").document(companyId)

                companyDocumentRef.collection("leaveRequests")
                    .add(leaveRequest).await()

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

    suspend fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val leaveRequestList = mutableListOf<LeaveRequest>()
                val companyCollection = firestore.collection("Companies").document(companyId)
                val leaveRequestCollection = companyCollection.collection("leaveRequests")
                val leaveRequestQuery = leaveRequestCollection.get().await()

                for (document in leaveRequestQuery) {
                    val leaveRequest = document.toObject(LeaveRequest::class.java)
                    leaveRequestList.add(leaveRequest)
                }

                emit(Result.Success(leaveRequestList))
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    suspend fun getCompanyMembers(companyId: String, role: User.UserRole? = null): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val companyMembersList = mutableListOf<User>()
                val companyCollection = firestore.collection("Companies").document(companyId)
                val membersCollection = companyCollection.collection("members")
                val membersQuery = membersCollection.get().await()

                for (document in membersQuery) {
                    val companyMember = document.toObject(User::class.java)
                    if (role == null || companyMember.role == role)
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


    // TODO: This deleteAccount deletes both account and company, use with caution. Need to separate it later
    suspend fun deleteAccount(userId: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val firebaseUser = firebaseAuth.currentUser
                val databaseUser = getUserData(userId)

                if (firebaseUser != null && databaseUser != null) {
                    firestore.collection("Users").document(userId).delete().await()

                    if (!databaseUser.companyId.isNullOrEmpty()) {
                        val companyId = databaseUser.companyId.toString()
                        val companyRef = firestore.collection("Companies").document(companyId)
                        val membersCollection = companyRef.collection("members")
                        membersCollection.document(userId).delete().await()
                    }

                    firebaseUser.delete().await()
                    delay(2000L)
                    signOut()
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error("Error: User Not Found"))
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

    fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    private suspend fun convertFirebaseUserToUser(firebaseUser: FirebaseUser) {
        val userRef = firestore.collection("Users").document(firebaseUser.uid)

        try {
            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) { // User doesn't exist, create a new user document
                    val newUser = User(
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName,
                        email = firebaseUser.email,
                        profilePicture = firebaseUser.photoUrl.toString()
                    )
                    userRef.set(newUser)
                    // TODO saveUserToDataStore(newUser)
                }
            }.await()
        } catch (exception: Exception) {
            // Handle errors here
        }
    }

    fun getCurrentUser(): String? {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.uid
    }

    suspend fun getUserData(userId: String): User? {
        val userDocument = firestore.collection("Users").document(userId).get().await()
        if (userDocument.exists()) {
            return userDocument.toObject(User::class.java)
        }
        return null
    }

    suspend fun getCompanyData(companyId: String): Company? {
        val companyDocument = firestore.collection("Companies").document(companyId).get().await()
        if (companyDocument.exists()) {
            return companyDocument.toObject(Company::class.java)
        }
        return null
    }

    private suspend fun saveUserToDataStore(user: User) {
        userPreferences.saveUser(user)
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