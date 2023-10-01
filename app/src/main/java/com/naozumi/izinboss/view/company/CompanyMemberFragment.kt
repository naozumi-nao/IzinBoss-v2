package com.naozumi.izinboss.view.company

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentCompanyMemberBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CompanyMemberFragment : DialogFragment() {
    private var _binding: FragmentCompanyMemberBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel
    private var user: User? = null
    private var clickedUser: User? = null

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userBundle =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable("user", User::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelable("user")
            }
        if (userBundle != null) {
            clickedUser = userBundle
        }

        _binding = FragmentCompanyMemberBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        binding?.progressBar?.visibility = View.GONE

        lifecycleScope.launch {
            user = viewModel.getUser().first()
            setUserData(clickedUser)
        }

        if(user?.role == User.UserRole.MANAGER && clickedUser?.uid != user?.uid) {
            binding?.btnKickFromCompany?.setOnClickListener(3000L) {
                lifecycleScope.launch {
                    kickUserFromCompany(clickedUser?.uid)
                }
            }
        } else {
            binding?.btnKickFromCompany?.visibility = View.GONE
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
                Glide.with(requireActivity())
                    .load(user.profilePicture)
                    .error(R.drawable.onboarding_image_1)
                    .into(ivProfilePhoto)
            }
        }
    }

    private suspend fun kickUserFromCompany(userId: String?) {
        viewModel.kickUserFromCompany(userId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "${user?.name} has been kicked",
                            Toast.LENGTH_SHORT
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
                            requireContext(),
                            "Error: " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(user: User): CompanyMemberFragment {
            val fragment = CompanyMemberFragment()
            val args = Bundle()
            args.putParcelable("user", user)
            fragment.arguments = args
            return fragment
        }
    }
}
