package com.naozumi.izinboss.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.naozumi.izinboss.R
import com.naozumi.izinboss.core.data.DataRepository
import com.naozumi.izinboss.core.data.UserPreferences

object Injection {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    fun provideRepository(context: Context): DataRepository {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val firebaseAuth = FirebaseAuth.getInstance()

        val firebaseDatabase = FirebaseDatabase.getInstance("https://izinboss-app-default-rtdb.firebaseio.com/")
        val databaseReference = firebaseDatabase.reference

        //val userPreferences = UserPreferences.getInstance(context.dataStore)

        return DataRepository.getInstance(firebaseAuth, googleSignInClient, databaseReference, provideDataStore(context))
    }

    fun provideDataStore(context: Context): UserPreferences {
        return UserPreferences.getInstance(context.dataStore)
    }
}