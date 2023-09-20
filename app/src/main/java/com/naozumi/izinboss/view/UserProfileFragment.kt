package com.naozumi.izinboss.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.databinding.FragmentProfileBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.entry.LoginActivity
import com.naozumi.izinboss.viewmodel.UserProfileViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: UserProfileViewModel
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

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        lifecycleScope.launch {
            user =  viewModel.getUser().first()
            setUserData(user)
        }

        binding?.btnDeleteAccount?.setOnClickListener(2000L) {
            lifecycleScope.launch {
                deleteUserData(user?.uid)
                viewModel.deleteCurrentUserDataStore()
            }
        }
    }

    private suspend fun setUserData(user: User?) {
        val company = viewModel.getCompanyData(user?.companyId.toString())
        binding?.apply {
            if (user != null) {
                tvFullNameInput.text = user.name
                tvCompanyInput.text = company?.name
                tvRoleInput.text = user.role.toString().lowercase().replaceFirstChar { it.uppercase() }
                tvUidInput.text = user.uid
                tvUidInput.setOnClickListener {
                    StringUtils.copyTextToClipboard(requireActivity(), tvUidInput.text)
                }
                tvEmailInput.text = user.email
                Glide.with(this@UserProfileFragment)
                    .load(user.profilePicture)
                    .error(R.drawable.onboarding_image_1)
                    .into(ivProfilePhoto)
            }
        }
    }

    private suspend fun deleteUserData(userId: String?) {
        viewModel.deleteAccount(userId).observe(viewLifecycleOwner) { result ->
            when(result){
                is Result.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    AlertDialog.Builder(requireActivity()).apply {
                        setTitle(getString(R.string.success))
                        setMessage(getString(R.string.account_deleted))
                        setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                            ViewUtils.moveActivityNoHistory(requireActivity(), LoginActivity::class.java)
                        }
                        create()
                        show()
                    }.apply {
                        setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                            ViewUtils.moveActivityNoHistory(requireActivity(), LoginActivity::class.java)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}