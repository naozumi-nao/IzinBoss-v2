package com.naozumi.izinboss.view.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentAddUserToCompanyBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.FormValidator
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import kotlinx.coroutines.runBlocking

class AddUserToCompanyFragment : DialogFragment() {
    private var _binding: FragmentAddUserToCompanyBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<CompanyViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var user: User? = null
    private lateinit var companyId: String

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        companyId = arguments?.getString("companyId").toString()
        _binding = FragmentAddUserToCompanyBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = viewModel.user

        val userRoleList: List<String> = User.UserRole.values().map { it.name }
        val roleAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            userRoleList
        )
        binding?.actvChooseUserRole?.setAdapter(roleAdapter)

        val textWatcher = FormValidator.createTextWatcherWithButton(
            binding?.btnAddUser,
            binding?.edUserIdInput,
            binding?.actvChooseUserRole
        )

        binding?.progressBar?.visibility = View.GONE
        binding?.btnAddUser?.isEnabled = false
        binding?.edUserIdInput?.addTextChangedListener(textWatcher)
        binding?.actvChooseUserRole?.addTextChangedListener(textWatcher)

        binding?.btnAddUser?.setOnClickListener(3000L) {
            addUserToCompany()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun addUserToCompany() {
        val userId = binding?.edUserIdInput?.text.toString()
        val newMember = runBlocking { viewModel.getUserData(userId) }
        val userRole = when (binding?.actvChooseUserRole?.text.toString()) {
            "MANAGER" -> User.UserRole.MANAGER
            "EMPLOYEE" -> User.UserRole.EMPLOYEE
            else -> User.UserRole.EMPLOYEE
        }
        viewModel.addUserToCompany(companyId, newMember, userRole)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                requireActivity(),
                                "Successfully added user to company",
                                Toast.LENGTH_LONG
                            ).show()
                            dismiss()
                            ViewUtils.replaceFragment(
                                requireActivity() as AppCompatActivity,
                                R.id.nav_main_content_container,
                                CompanyProfileFragment(),
                                CompanyProfileFragment::class.java.simpleName
                            )
                        }
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                requireActivity(),
                                "Error: " + result.error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }

    companion object {
        fun newInstance(companyId: String): AddUserToCompanyFragment {
            val fragment = AddUserToCompanyFragment()
            val args = Bundle()
            args.putString("companyId", companyId)
            fragment.arguments = args
            return fragment
        }
    }
}