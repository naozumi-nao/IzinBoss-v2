package com.naozumi.izinboss.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        val firebaseDatabase = FirebaseDatabase.getInstance("https://izinboss-app-default-rtdb.firebaseio.com/")
        val databaseReference = firebaseDatabase.reference

        return UserRepository.getInstance(firebaseAuth, googleSignInClient, databaseReference)
    }
}