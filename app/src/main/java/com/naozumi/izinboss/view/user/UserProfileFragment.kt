package com.naozumi.izinboss.view.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.databinding.FragmentProfileBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.view.entry.LoginActivity
import com.naozumi.izinboss.viewmodel.user.UserProfileViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<UserProfileViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.progressBar?.visibility = View.GONE

        user =  viewModel.user
        setUserData(user)

        if (user?.companyId.isNullOrEmpty()) {
            binding?.btnLeaveCurrentCompany?.visibility = View.GONE
        }

        binding?.btnProfileInfo?.setOnClickListener(3000L) {
            ChangeNameFragment().show(parentFragmentManager, "Change Name Dialog")
        }

        binding?.btnLeaveCurrentCompany?.setOnClickListener(3000L) {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Warning")
                setMessage("Are you sure you want to leave your company?")
                setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch {
                        leaveCurrentCompany(user?.uid)
                    }
                }
                setNegativeButton("No") { _, _ -> }
                create()
                show()
            }
        }

        binding?.btnDeleteAccount?.setOnClickListener(3000L) {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Warning")
                setMessage("Are you sure you want to delete your account?")
                setPositiveButton("Yes") { _, _ ->
                    deleteUserAccount(user?.uid)
                }
                setNegativeButton("No") { _, _ -> }
                create()
                show()
            }
        }
    }

    private fun setUserData(user: User?) {
        binding?.apply {
            if (user != null) {
                tvFullNameInput.text = user.name
                tvCompanyInput.text = user.companyName
                if (user.role != null) {
                    tvRoleInput.text = user.role.toString().lowercase().replaceFirstChar { it.uppercase() }
                } else {
                    tvRoleInput.text = ""
                }
                tvUidInput.text = user.uid
                tvUidInput.setOnClickListener {
                    StringUtils.copyTextToClipboard(requireActivity(), tvUidInput.text)
                }
                tvEmailInput.text = user.email
                Glide.with(this@UserProfileFragment)
                    .load(user.profilePicture)
                    .error(R.drawable.baseline_person_24)
                    .into(ivProfilePhoto)
            }
        }
    }

    private fun leaveCurrentCompany(userId: String?) {
        viewModel.leaveCurrentCompany(userId).observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    ViewUtils.showContinueDialog(
                        requireActivity(),
                        getString(R.string.success),
                        "You Have Left From Your Company",
                        MainActivity::class.java
                    )
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

    private fun deleteUserAccount(userId: String?) {
        viewModel.deleteAccount(userId).observe(viewLifecycleOwner) { result ->
            when(result){
                is Result.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    ViewUtils.showContinueDialog(
                        requireActivity(),
                        getString(R.string.success),
                        "Account Deleted",
                        LoginActivity::class.java
                    )
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}