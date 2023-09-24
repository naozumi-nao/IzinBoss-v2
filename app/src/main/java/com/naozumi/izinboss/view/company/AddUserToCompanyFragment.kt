package com.naozumi.izinboss.view.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentAddUserToCompanyBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.TextInputUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.HomeFragment
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import kotlinx.coroutines.launch

class AddUserToCompanyFragment : DialogFragment() {
    private var _binding: FragmentAddUserToCompanyBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel
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

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        val userRoleList: List<String> = User.UserRole.values().map { it.name }
        val roleAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            userRoleList
        )
        binding?.actvChooseUserRole?.setAdapter(roleAdapter)

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding?.btnAddUser,
            binding?.edUserIdInput,
            binding?.actvChooseUserRole
        )

        binding?.progressBar?.visibility = View.GONE
        binding?.btnAddUser?.isEnabled = false
        binding?.edUserIdInput?.addTextChangedListener(textWatcher)
        binding?.actvChooseUserRole?.addTextChangedListener(textWatcher)

        binding?.btnAddUser?.setOnClickListener(3000L) {
            lifecycleScope.launch {
                addUserToCompany()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private suspend fun addUserToCompany() {
        val userId = binding?.edUserIdInput?.text.toString()
        val userRole = when (binding?.actvChooseUserRole?.text.toString()) {
            "MANAGER" -> User.UserRole.MANAGER
            "EMPLOYEE" -> User.UserRole.EMPLOYEE
            else -> User.UserRole.EMPLOYEE
        }
        user = viewModel.getUserData(userId)
        viewModel.addUserToCompany(companyId, user, userRole)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            AlertDialog.Builder(requireActivity()).apply {
                                setTitle(getString(R.string.success))
                                setMessage("Successfully Added User to Company!")
                                setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                    dismiss()
                                    ViewUtils.replaceFragment(
                                        requireActivity() as AppCompatActivity,
                                        R.id.nav_main_content_container,
                                        HomeFragment(),
                                        HomeFragment::class.java.simpleName
                                    )
                                }
                                create()
                                show()
                            }.apply {
                                setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                    dismiss()
                                    ViewUtils.replaceFragment(
                                        requireActivity() as AppCompatActivity,
                                        R.id.nav_main_content_container,
                                        HomeFragment(),
                                        HomeFragment::class.java.simpleName
                                    )
                                }
                                show()
                            }
                        }
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            AlertDialog.Builder(requireActivity()).apply {
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