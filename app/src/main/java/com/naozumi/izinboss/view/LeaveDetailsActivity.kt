package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.naozumi.izinboss.databinding.ActivityLeaveDetailsBinding

class LeaveDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}