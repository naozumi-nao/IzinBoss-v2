package com.naozumi.izinboss.view.company

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentCompanyProfileBinding
import com.naozumi.izinboss.model.adapter.UserListAdapter
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CompanyProfileFragment : Fragment() {
    private var _binding: FragmentCompanyProfileBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<CompanyViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var user: User? = null
    private val userListAdapter = UserListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompanyProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.progressBar?.visibility = View.GONE
        user = viewModel.user

        getCompany(user?.companyId.toString())
        getCompanyMembers()

        if(user?.role == User.UserRole.MANAGER) {
            binding?.btnCompanyMembers?.setOnClickListener(3000L) {
                val addUserFragment =
                    AddUserToCompanyFragment.newInstance(user?.companyId.toString())
                addUserFragment.show(parentFragmentManager, "companyId")
            }
        }
    }

    private fun setCompanyData(company: Company) {
        binding?.apply {
            tvCompanyNameInput.text = company.name
            tvCompanyIdInput.text = company.id
            tvIndustrySectorInput.text =
                StringUtils.capitalizeWordsExceptAnd(company.industrySector.toString().lowercase())
            tvCompanyIdInput.setOnClickListener {
                StringUtils.copyTextToClipboard(requireContext(), tvCompanyIdInput.text)
            }
        }
    }

    private fun getCompany(companyId: String) {
        viewModel.getCompanyData(companyId).observe(viewLifecycleOwner) { result ->
            when(result)  {
                is Result.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    setCompanyData(result.data)
                }
                is Result.Error -> {
                    binding?.progressBar?.visibility = View.GONE
                    ViewUtils.showContinueDialog(
                        requireActivity(),
                        getString(R.string.error),
                        result.error
                    )
                }
            }
        }
    }

    private fun getCompanyMembers() {
        userListAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val companyMemberFragment = CompanyMemberFragment.newInstance(data)
                companyMemberFragment.show(parentFragmentManager, "user")
            }
        })
        viewModel.getCompanyMembers(user?.companyId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        val memberData = result.data
                        userListAdapter.submitList(memberData)
                    }
                    is Result.Error -> {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error: " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding?.rvMembersList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val divider = MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            addItemDecoration(divider)
            setHasFixedSize(true)
            adapter = userListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
