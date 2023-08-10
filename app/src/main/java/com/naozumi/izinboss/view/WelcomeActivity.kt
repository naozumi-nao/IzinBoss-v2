package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.naozumi.izinboss.databinding.ActivityWelcomeBinding
import com.naozumi.izinboss.util.ViewUtils

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)
        setupAction()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            ViewUtils.moveActivity(this, RegisterActivity::class.java)
        }
        binding.btnLogin.setOnClickListener {
            ViewUtils.moveActivity(this, LoginActivity::class.java)
        }
    }
}