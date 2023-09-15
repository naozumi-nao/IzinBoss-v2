package com.naozumi.izinboss.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.databinding.FragmentCreateCompanyBinding
import com.naozumi.izinboss.viewmodel.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class CreateCompanyFragment : Fragment() {
    private var _binding: FragmentCreateCompanyBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCompanyBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        binding?.progressBar?.visibility = View.GONE
        binding?.btnRegisterCompany?.setOnClickListener {
            lifecycleScope.launch {
                registerCompany(viewModel.getCurrentUser())
            }
        }
    }

    private suspend fun registerCompany(userId: String?) {
        val name = binding?.edRegisterCompanyName?.text.toString()

        viewModel.createCompany(name, userId.toString()).observe(viewLifecycleOwner) {result ->
            if(result != null) {
                when(result) {
                    is Result.Loading -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        AlertDialog.Builder(requireActivity()).apply {
                            setTitle(getString(R.string.success))
                            setMessage("Company Created!")
                            setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                ViewUtils.moveActivityNoHistory(requireActivity(), MainActivity::class.java)
                            }
                            create()
                            show()
                        }.apply {
                            setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                ViewUtils.moveActivityNoHistory(requireActivity(), MainActivity::class.java)
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