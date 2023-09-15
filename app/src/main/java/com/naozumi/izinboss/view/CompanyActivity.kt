package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityCompanyBinding
import com.naozumi.izinboss.model.adapter.MembersSectionsPagerAdapter
import com.naozumi.izinboss.viewmodel.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class CompanyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompanyBinding
    private lateinit var viewModel: CompanyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        lifecycleScope.launch {
            val user = viewModel.getUserData(viewModel.getCurrentUser().toString())
            setCompanyData(user?.companyId.toString())
        }

        val membersSectionsPagerAdapter = MembersSectionsPagerAdapter(this)
        binding.viewPager.adapter = membersSectionsPagerAdapter
        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        binding.progressBar.visibility = View.GONE
    }

    private suspend fun setCompanyData(companyId: String) {
        val company = viewModel.getCompanyData(companyId)

        if (company != null) {
            binding.apply {
                tvCompanyNameInput.text = company.name
                tvCompanyIdInput.text = company.id
            }
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.employees, R.string.managers)
    }
}
