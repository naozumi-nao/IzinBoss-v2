package com.naozumi.izinboss.model.repo

import android.content.Intent
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result

interface UserRepository {
    suspend fun signInWithGoogle(idToken: String): LiveData<Result<FirebaseUser>>
    suspend fun registerWithEmail(name: String, email: String, password: String): LiveData<Result<FirebaseUser>>
    suspend fun loginWithEmail(email: String, password: String): LiveData<Result<Unit>>
    suspend fun changeFullName(newName: String, user: User?): LiveData<Result<Unit>>
    suspend fun deleteAccount(userId: String?): LiveData<Result<Unit>>
    suspend fun convertFirebaseUserToUser(firebaseUser: FirebaseUser)
    fun signOut()
    fun getSignInIntent(): Intent
    fun getCurrentUserID(): String?
    suspend fun getUserData(userId: String?): User?
}
