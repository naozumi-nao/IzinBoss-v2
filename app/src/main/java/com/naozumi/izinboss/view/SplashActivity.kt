package com.naozumi.izinboss.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.core.data.SettingsPreferences
import com.naozumi.izinboss.core.data.UserPreferences
import com.naozumi.izinboss.core.util.ViewUtils
import com.naozumi.izinboss.viewmodel.ProfileViewModel
import com.naozumi.izinboss.viewmodel.SettingsViewModel
import com.naozumi.izinboss.viewmodel.SettingsViewModelFactory
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ViewUtils.setupFullScreen(this)

        val content = findViewById<View>(android.R.id.content)
        @Suppress("UNUSED_EXPRESSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false }
        }

        val pref = SettingsPreferences.getInstance(dataStore)
        val settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(pref))[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        val viewModel: ProfileViewModel by viewModels {
            factory
        }

        lifecycleScope.launch {
            delay(500.toLong())

            val userLogin = runBlocking {
                viewModel.getCurrentUser()
            }

            if (userLogin != null)
                ViewUtils.moveActivityNoHistory(this@SplashActivity, MainActivity::class.java)
            else
                ViewUtils.moveActivityNoHistory(this@SplashActivity, OnboardingActivity::class.java)
        }
    }
}