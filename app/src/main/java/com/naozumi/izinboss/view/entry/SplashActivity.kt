package com.naozumi.izinboss.view.entry

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.viewmodel.user.UserProfileViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        val viewModel: UserProfileViewModel by viewModels {
            factory
        }

        lifecycleScope.launch {
            delay(500.toLong())

            if (viewModel.user != null)
                ViewUtils.moveActivityNoHistory(this@SplashActivity, MainActivity::class.java)
            else
                ViewUtils.moveActivityNoHistory(this@SplashActivity, OnboardingActivity::class.java)
        }
    }
}