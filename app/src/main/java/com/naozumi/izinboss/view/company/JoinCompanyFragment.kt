package com.naozumi.izinboss.view.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentJoinCompanyBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.TextInputUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class JoinCompanyFragment : DialogFragment() {
    private var _binding: FragmentJoinCompanyBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel
    private var user: User? = null

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

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding?.btnJoinCompany,
            binding?.edRegisterCompanyName
        )

        binding?.progressBar?.visibility = View.GONE
        binding?.btnJoinCompany?.isEnabled = false
        binding?.edRegisterCompanyName?.addTextChangedListener(textWatcher)

        binding?.btnJoinCompany?.setOnClickListener(3000L) {
            lifecycleScope.launch {
                user =  viewModel.getUser().first()
                joinCompany()
            }
            // Handle button click
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private suspend fun joinCompany() {
        val companyName = binding?.edRegisterCompanyName?.text.toString()
        viewModel.addUserToCompany(companyName, user, user?.role)
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
                                setMessage("Successfully Joined a Company!")
                                setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                    ViewUtils.moveActivityNoHistory(
                                        requireActivity(),
                                        MainActivity::class.java
                                    )
                                }
                                create()
                                show()
                            }.apply {
                                setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                    ViewUtils.moveActivityNoHistory(
                                        requireActivity(),
                                        MainActivity::class.java
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

}