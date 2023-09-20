package com.naozumi.izinboss.view.company

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityCreateCompanyBinding
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.TextInputUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.view.entry.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CreateCompanyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCompanyBinding
    private lateinit var viewModel: CompanyViewModel
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        val industrySectorList: List<String> = Company.IndustrySector.values().map { type ->
            StringUtils.capitalizeWordsExceptAnd(type.name.lowercase())
        }
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            industrySectorList
        )
        binding.actvSelectIndustrySector.setAdapter(typeAdapter)
        binding.progressBar.visibility = View.GONE

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding.btnRegisterCompany,
            binding.edRegisterCompanyName,
            binding.actvSelectIndustrySector
        )

        binding.btnRegisterCompany.isEnabled = false
        binding.edRegisterCompanyName.addTextChangedListener(textWatcher)
        binding.actvSelectIndustrySector.addTextChangedListener(textWatcher)

        binding.btnRegisterCompany.setOnClickListener(3000L) {
            lifecycleScope.launch {
                user = viewModel.getUser().first()
                registerCompany()
            }
        }

        binding.btnJoinCompany.setOnClickListener {
            val joinCompanyFragment = JoinCompanyFragment()
            joinCompanyFragment.show(supportFragmentManager, "JoinCompanyDialog")
        }
    }

    private suspend fun registerCompany() {
        val companyName = binding.edRegisterCompanyName.text.toString()
        val industrySectorString = binding.actvSelectIndustrySector.text.toString().trim()
        val selectedIndustrySector = StringUtils.industrySectorMap[industrySectorString]

        viewModel.createCompany(companyName, selectedIndustrySector, user)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            AlertDialog.Builder(this@CreateCompanyActivity).apply {
                                setTitle(getString(R.string.success))
                                setMessage("Company Created!")
                                setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                    ViewUtils.moveActivityNoHistory(
                                        this@CreateCompanyActivity,
                                        MainActivity::class.java
                                    )
                                }
                                create()
                                show()
                            }.apply {
                                setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                    ViewUtils.moveActivityNoHistory(
                                        this@CreateCompanyActivity,
                                        MainActivity::class.java
                                    )
                                }
                                show()
                            }
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            AlertDialog.Builder(this@CreateCompanyActivity).apply {
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
