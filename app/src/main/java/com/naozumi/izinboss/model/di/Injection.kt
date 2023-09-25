package com.naozumi.izinboss.model.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.repo.DataRepository
import com.naozumi.izinboss.model.repo.UserPreferences

object Injection {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    fun provideRepository(context: Context): DataRepository {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val firebaseAuth = FirebaseAuth.getInstance()

        val firestore = Firebase.firestore
        val storage = Firebase.storage("gs://izinboss-app.appspot.com")

        return DataRepository.getInstance(firebaseAuth, googleSignInClient, firestore, storage, provideDataStore(context))
    }

    fun provideDataStore(context: Context): UserPreferences {
        return UserPreferences.getInstance(context.dataStore)
    }
}