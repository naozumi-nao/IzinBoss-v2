package com.naozumi.izinboss.model.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.naozumi.izinboss.model.datamodel.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor (private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            val uid = preferences[UID_KEY] ?: ""
            val name = preferences[NAME_KEY] ?: ""
            val email = preferences[EMAIL_KEY] ?: ""
            val profilePicture = preferences[PROFILE_PICTURE_KEY] ?: ""
            val companyId = preferences[COMPANY_ID_KEY] ?: ""
            val role =
                if (preferences[ROLE_KEY]?.isNotBlank() == true) {
                    User.UserRole.valueOf(preferences[ROLE_KEY].toString())
                } else {
                    null
                }
            User(uid, name, email, profilePicture, companyId, role)
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[UID_KEY] = user.uid ?: ""
            preferences[NAME_KEY] = user.name ?: ""
            preferences[EMAIL_KEY] = user.email ?: ""
            preferences[PROFILE_PICTURE_KEY] = user.profilePicture ?: ""
            preferences[COMPANY_ID_KEY] = user.companyId ?: ""
            preferences[ROLE_KEY] = user.role?.name ?: ""
        }
    }

    suspend fun deleteCurrentUserPref() {
        dataStore.edit {
            it.clear()
        }
        /*dataStore.edit { preferences ->
            preferences[UID_KEY] = ""
            preferences[NAME_KEY] = ""
            preferences[EMAIL_KEY] = ""
            preferences[PROFILE_PICTURE_KEY] = ""
            preferences[COMPANY_ID_KEY] = ""
            preferences[ROLE_KEY] = ""
        }
         */
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val UID_KEY = stringPreferencesKey("uid")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PROFILE_PICTURE_KEY = stringPreferencesKey("profile_picture")
        private val COMPANY_ID_KEY = stringPreferencesKey("company_id")
        private val ROLE_KEY = stringPreferencesKey("role")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}