package com.naozumi.izinboss.view.entry

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naozumi.izinboss.databinding.ActivityOnboardingBinding
import com.naozumi.izinboss.model.util.ViewUtils

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)

        binding.btnSkip.setOnClickListener {
            ViewUtils.moveActivityNoHistory(this, LoginActivity::class.java)
        }

        binding.btnStart.setOnClickListener {
            ViewUtils.moveActivityNoHistory(this, LoginActivity::class.java)
        }
    }
}