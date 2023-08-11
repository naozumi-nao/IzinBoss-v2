package com.naozumi.izinboss.view

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.naozumi.izinboss.R
import com.naozumi.izinboss.data.Result
import com.naozumi.izinboss.databinding.ActivityLoginBinding
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.LoginViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)
        binding.progressBar.visibility = View.GONE

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            setupEmailLogin()
        }

        binding.btnGoogleSignIn.setOnClickListener {
            resultLauncher.launch(viewModel.getSignInIntent())
        }

        binding.btnForgotPassword.setOnClickListener {
            // TODO(add forgot password)
        }

        binding.btnCreateAccount.setOnClickListener {
            ViewUtils.moveActivity(this, RegisterActivity::class.java)
        }
    }

    private suspend fun setupGoogleSignIn(token: String) {
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
                            ViewUtils.moveActivityNoHistory(this@LoginActivity, MainActivity::class.java)
                        }
                        is Result.Error -> {
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

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)!!
            lifecycleScope.launch {
                setupGoogleSignIn(account.idToken.toString())
            }
        }
    }

}