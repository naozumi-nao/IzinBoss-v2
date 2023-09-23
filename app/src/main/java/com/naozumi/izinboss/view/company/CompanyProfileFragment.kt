package com.naozumi.izinboss.view.company

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentCompanyProfileBinding
import com.naozumi.izinboss.model.adapter.UserListAdapter
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CompanyProfileFragment : Fragment() {
    private var _binding: FragmentCompanyProfileBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel
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

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        binding?.progressBar?.visibility = View.GONE

        lifecycleScope.launch {
            user = viewModel.getUser().first()
            setCompanyData(user?.companyId.toString())
            getCompanyMembers()
        }
    }

    private suspend fun setCompanyData(companyId: String) {
        val company = viewModel.getCompanyData(companyId)

        if (company != null) {
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
    }

    private suspend fun getCompanyMembers() {
        userListAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Toast.makeText(
                    requireContext(),
                    "Clicked on user",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO move to User Profile
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
