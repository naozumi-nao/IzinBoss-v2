package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.data.Result
import com.naozumi.izinboss.databinding.ActivityCreateCompanyBinding
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.CreateCompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class CreateCompanyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCompanyBinding
    private lateinit var viewModel: CreateCompanyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("data")

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[CreateCompanyViewModel::class.java]

        binding.progressBar.visibility = View.GONE
        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                registerCompany(userId)
            }
        }
    }

    private suspend fun registerCompany(userId: String?) {
        val name = binding.edRegisterName.text.toString()

        viewModel.createCompany(name, userId.toString()).observe(this) {result ->
            if(result != null) {
                when(result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success))
                            setMessage("Company Created!")
                            setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                ViewUtils.moveActivityNoHistory(this@CreateCompanyActivity, MainActivity::class.java)
                            }
                            create()
                            show()
                        }.apply {
                            setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                ViewUtils.moveActivityNoHistory(this@CreateCompanyActivity, MainActivity::class.java)
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