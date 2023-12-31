package com.naozumi.izinboss.view.company

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentCreateCompanyBinding
import com.naozumi.izinboss.model.datamodel.Company
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.FormValidator
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory

class CreateCompanyFragment : Fragment() {
    private var _binding: FragmentCreateCompanyBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<CompanyViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCompanyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val industrySectorList: List<String> = Company.IndustrySector.values().map { type ->
            StringUtils.capitalizeWordsExceptAnd(type.name.lowercase())
        }
        val typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            industrySectorList
        )
        binding?.actvSelectIndustrySector?.setAdapter(typeAdapter)
        binding?.progressBar?.visibility = View.GONE

        val textWatcher = FormValidator.createTextWatcherWithButton(
            binding?.btnRegisterCompany,
            binding?.edRegisterCompanyName,
            binding?.actvSelectIndustrySector
        )

        binding?.btnRegisterCompany?.isEnabled = false
        binding?.edRegisterCompanyName?.addTextChangedListener(textWatcher)
        binding?.actvSelectIndustrySector?.addTextChangedListener(textWatcher)

        binding?.btnRegisterCompany?.setOnClickListener(3000L) {
            user = viewModel.user
            registerCompany()
        }

        binding?.btnAlreadyHaveCompany?.setOnClickListener {
            JoinCompanyFragment().show(parentFragmentManager, "JoinCompanyDialog")
        }
    }

    private fun registerCompany() {
        val companyName = binding?.edRegisterCompanyName?.text.toString()
        val industrySectorString = binding?.actvSelectIndustrySector?.text.toString().trim()
        val selectedIndustrySector = StringUtils.industrySectorMap[industrySectorString]

        viewModel.createCompany(companyName, selectedIndustrySector, user)
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle(getString(R.string.success))
                                setMessage("Company Created!")
                                setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                    ViewUtils.moveActivityNoHistory(
                                        requireContext(),
                                        MainActivity::class.java
                                    )
                                }
                                create()
                                show()
                            }.apply {
                                setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                    ViewUtils.moveActivityNoHistory(
                                        requireContext(),
                                        MainActivity::class.java
                                    )
                                }
                                show()
                            }
                        }
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            AlertDialog.Builder(requireContext()).apply {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
