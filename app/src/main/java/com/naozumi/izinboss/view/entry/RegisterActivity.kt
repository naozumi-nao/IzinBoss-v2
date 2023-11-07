package com.naozumi.izinboss.view.entry

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityRegisterBinding
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewUtils.setupFullScreen(this)

        binding.progressBar.visibility = View.GONE
        binding.btnRegisterUser.setOnClickListener {
            setupRegister()
        }
    }

    private fun setupRegister() {
        val name = binding.edRegisterFullName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        when {
            name.isEmpty() -> {
                binding.edRegisterFullName.error = getString(R.string.input_name)
            }
            email.isEmpty() -> {
                binding.edRegisterEmail.error = getString(R.string.input_email)
            }
            !ViewUtils.isValidEmail(email) -> {
                binding.edRegisterPassword.error = getString(R.string.invalid_email)
            }
            password.isEmpty() -> {
                binding.edRegisterPassword.error = getString(R.string.input_password)
            }
            password.length < 8 -> {
                binding.edRegisterPassword.error = getString(R.string.pass_less_than_8_char)
            }
            else -> {
                viewModel.registerWithEmail(name, email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                ViewUtils.showContinueDialog(
                                    this@RegisterActivity,
                                    getString(R.string.success),
                                    getString(R.string.account_created),
                                    LoginActivity::class.java
                                )
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                ViewUtils.showContinueDialog(
                                    this@RegisterActivity,
                                    getString(R.string.error),
                                    result.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}