package com.naozumi.izinboss.model.repo

import android.content.Intent
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun signInWithGoogle(idToken: String): Flow<Result<Unit>>
    fun registerWithEmail(name: String, email: String, password: String): Flow<Result<Unit>>
    fun loginWithEmail(email: String, password: String): Flow<Result<Unit>>
    fun changeFullName(newName: String, user: User?): Flow<Result<Unit>>
    fun deleteAccount(userId: String?): Flow<Result<Unit>>
    fun signOut()
    suspend fun convertFirebaseUserToUser(firebaseUser: FirebaseUser)
    fun getSignInIntent(): Intent
    fun getCurrentUserID(): String?
    suspend fun getUserData(userId: String?): User?

    //suspend fun saveUser(user: User)
    //fun getUser(): Flow<User>
    //suspend fun logout()
}
