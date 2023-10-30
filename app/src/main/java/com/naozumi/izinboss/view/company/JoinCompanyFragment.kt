package com.naozumi.izinboss.view.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentJoinCompanyBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.TextInputUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.entry.RegisterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class JoinCompanyFragment : DialogFragment() {
    private var _binding: FragmentJoinCompanyBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<CompanyViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var user: User? = null

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJoinCompanyBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding?.btnJoinCompany,
            binding?.edCompanyIdInput
        )

        binding?.progressBar?.visibility = View.GONE
        binding?.btnJoinCompany?.isEnabled = false
        binding?.edCompanyIdInput?.addTextChangedListener(textWatcher)

        binding?.btnJoinCompany?.setOnClickListener(3000L) {
            lifecycleScope.launch {
                user =  viewModel.getUser().first()
            }
            joinCompany()
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun joinCompany() {
        val companyId = binding?.edCompanyIdInput?.text.toString()
        viewModel.addUserToCompany(companyId, user, user?.role)
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
                                "Successfully joined a company",
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
}