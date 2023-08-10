package com.naozumi.izinboss.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.naozumi.izinboss.R
import com.naozumi.izinboss.data.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val firebaseAuth = FirebaseAuth.getInstance()

        //val database = UserDatabase.getInstance(context)
        //val dao = database.userDao()
        return UserRepository.getInstance(firebaseAuth, googleSignInClient)
    }
}