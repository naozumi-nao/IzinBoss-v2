package com.naozumi.izinboss.view.entry

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityRegisterBinding
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewUtils.setupFullScreen(this)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        binding.progressBar.visibility = View.GONE
        binding.btnRegisterUser.setOnClickListener {
            lifecycleScope.launch {
                setupRegister()
            }
        }
    }

    private suspend fun setupRegister() {
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
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.success))
                                    setMessage(getString(R.string.account_created))
                                    setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                        ViewUtils.moveActivityNoHistory(
                                            this@RegisterActivity,
                                            LoginActivity::class.java
                                        )
                                    }
                                    create()
                                    show()
                                }.apply {
                                    setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                        ViewUtils.moveActivityNoHistory(
                                            this@RegisterActivity,
                                            LoginActivity::class.java
                                        )
                                    }
                                    show()
                                }
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.error))
                                    setMessage(result.error)
                                    setPositiveButton(getString(R.string.continue_on)) { _, _ -> }
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}