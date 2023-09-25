package com.naozumi.izinboss.view.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentChangeNameBinding
import com.naozumi.izinboss.databinding.FragmentJoinCompanyBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.TextInputUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.company.CompanyProfileFragment
import com.naozumi.izinboss.viewmodel.UserProfileViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChangeNameFragment : DialogFragment() {
    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: UserProfileViewModel
    private var user: User? = null

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeNameBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding?.btnConfirmNewName,
            binding?.edNewNameInput
        )

        binding?.progressBar?.visibility = View.GONE
        binding?.btnConfirmNewName?.isEnabled = false
        binding?.edNewNameInput?.addTextChangedListener(textWatcher)

        binding?.btnConfirmNewName?.setOnClickListener(3000L) {
            lifecycleScope.launch {
                user =  viewModel.getUser().first()
                changeFullName()
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

    private suspend fun changeFullName() {
        val newName = binding?.edNewNameInput?.text.toString()
        viewModel.changeFullName(newName, user)
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
                                "Successfully changed your name",
                                Toast.LENGTH_LONG
                            ).show()
                            dismiss()
                            ViewUtils.replaceFragment(
                                requireActivity() as AppCompatActivity,
                                R.id.nav_main_content_container,
                                UserProfileFragment(),
                                UserProfileFragment::class.java.simpleName
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