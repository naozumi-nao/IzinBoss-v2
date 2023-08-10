package com.naozumi.izinboss.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.naozumi.izinboss.R
import com.naozumi.izinboss.util.ViewUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ViewUtils.setupFullScreen(this)

        val content = findViewById<View>(android.R.id.content)
        @Suppress("UNUSED_EXPRESSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false } // prevents double splash screens on android 12 or higher
        }

        lifecycleScope.launch {
            delay(500.toLong())
            ViewUtils.moveActivityNoHistory(this@SplashActivity, OnboardingActivity::class.java)
            /*if () {
                // Not signed in, launch the Login activity
                ViewUtils.moveActivityNoHistory(this@SplashActivity, OnboardingActivity::class.java)
            } else
            {
                // already signed in
                ViewUtils.moveActivityNoHistory(this@SplashActivity, MainActivity::class.java)
            }

             */
        }
    }
}