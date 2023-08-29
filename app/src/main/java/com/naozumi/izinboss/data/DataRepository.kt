package com.naozumi.izinboss.data

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.naozumi.izinboss.model.local.Leave
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
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getAllLeaves(): LiveData<Result<List<Leave>>> = liveData {
        emit(Result.Loading)
        try {
            val dataSnapShot = databaseReference.child("Leaves").get().await()
            val leaveList = mutableListOf<Leave>()

            dataSnapShot.children.forEach { leaveSnapShot ->
                val leave = leaveSnapShot.getValue(Leave::class.java)
                leave?.let {
                    leaveList.add(it)
                }
            }
            emit(Result.Success(leaveList))
        }  catch (e: FirebaseAuthException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun addLeaveToDatabase(leave: Leave): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)

        val leaveID = databaseReference.child("Leaves").push().key

        if (leaveID != null) {
            leave.id = leaveID
            try {
                databaseReference.child("Leaves").child(leaveID).setValue(leave).await()
                emit(Result.Success(Unit))
            } catch (e: FirebaseAuthException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        } else {
            emit(Result.Error("Failed to generate leave ID"))
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