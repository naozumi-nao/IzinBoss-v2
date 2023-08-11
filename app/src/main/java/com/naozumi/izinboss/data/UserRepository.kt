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
import kotlinx.coroutines.tasks.await

class UserRepository (private val firebaseAuth: FirebaseAuth, private var googleSignInClient: GoogleSignInClient) {

    suspend fun signInWithGoogle(idToken: String): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                //user.sendEmailVerification()
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

    fun loginWithEmail(email: String, password: String): LiveData<Result<FirebaseUser>> = liveData {
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

    fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(firebaseAuth: FirebaseAuth, googleSignInClient: GoogleSignInClient): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(firebaseAuth, googleSignInClient)
            }.also { instance = it }
        }
    }
}