package com.naozumi.izinboss.view.entry

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityLoginBinding
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.viewmodel.entry.LoginViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)!!

            setupGoogleSignIn(account.idToken.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)
        binding.progressBar.visibility = View.GONE

        binding.btnLogin.setOnClickListener {
            setupEmailLogin()
        }

        binding.btnGoogleSingleSignOn.setOnClickListener {
            resultLauncher.launch(viewModel.getSignInIntent())
        }

        binding.btnForgotPassword.setOnClickListener {
            // TODO(add forgot password)
        }

        binding.btnCreateAccount.setOnClickListener {
            ViewUtils.moveActivity(this, RegisterActivity::class.java)
        }
    }

    private fun setupGoogleSignIn(token: String) {
        viewModel.signInWithGoogle(token).observe(this) { result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    ViewUtils.moveActivityNoHistory(this@LoginActivity, MainActivity::class.java)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    ViewUtils.showContinueDialog(
                        this@LoginActivity,
                        getString(R.string.error),
                        result.error
                    )
                }
            }
        }
    }

    private fun setupEmailLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        when {
            email.isEmpty() -> {
                binding.edLoginEmail.error = getString(R.string.input_email)
            }
            password.isEmpty() -> {
                binding.edLoginPassword.error = getString(R.string.input_password)
            }
            else -> {
                viewModel.loginWithEmail(email, password).observe(this) { result ->
                    when(result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            ViewUtils.moveActivityNoHistory(
                                this@LoginActivity,
                                MainActivity::class.java
                            )
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            ViewUtils.showContinueDialog(
                                this@LoginActivity,
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