package com.naozumi.izinboss.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentCompanyBinding
import com.naozumi.izinboss.model.adapter.MembersSectionsPagerAdapter
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.viewmodel.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
/*
class CompanyFragment : Fragment() {
    private var _binding: FragmentCompanyBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionsPagerAdapter = MembersSectionsPagerAdapter(requireActivity())

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        lifecycleScope.launch {
            val user = viewModel.getUserData(viewModel.getCurrentUser().toString())
            sectionsPagerAdapter.companyId = user?.companyId.toString()
            setCompanyData(user?.companyId.toString())
            Toast.makeText(
                context,
                "Value: ${user?.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding?.viewPager?.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding?.tabs ?: TabLayout(requireContext()),
            binding?.viewPager ?: ViewPager2(requireContext())
        ) { tab, position ->
            tab.text = requireContext().resources.getString(TAB_TITLES[position])
        }.attach()

        binding?.progressBar?.visibility = View.GONE
    }

    private suspend fun setCompanyData(companyId: String) {
        val company = viewModel.getCompanyData(companyId)

        binding?.apply {
            if (company != null) {
                tvCompanyNameInput.text = company.name
                tvCompanyIdInput.text = company.id
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.employees,
            R.string.managers
        )
    }
}

 */