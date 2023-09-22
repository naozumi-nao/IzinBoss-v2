package com.naozumi.izinboss.view

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
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentLeaveRequestDetailsBinding
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LeaveRequestDetailsFragment : DialogFragment() {
    private var _binding: FragmentLeaveRequestDetailsBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel
    private var leaveRequest: LeaveRequest? = null
    private var user: User? = null

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val leaveRequestBundle =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable("leaveRequest", LeaveRequest::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelable("leaveRequest")
            }
        if (leaveRequestBundle != null) {
            leaveRequest = leaveRequestBundle
        }

        _binding = FragmentLeaveRequestDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]
        lifecycleScope.launch {
            user = viewModel.getUser().first()
        }

        when (user?.role) {
            User.UserRole.MANAGER -> {
                binding?.btnApprove?.setOnClickListener(3000L) {
                    lifecycleScope.launch {
                        changeLeaveRequestStatus(leaveRequest, true)
                    }
                }
                binding?.btnReject?.setOnClickListener(3000L) {
                    lifecycleScope.launch {
                        changeLeaveRequestStatus(leaveRequest, false)
                    }
                }
            }
            else -> {
                binding?.btnApprove?.visibility = View.GONE
                binding?.btnReject?.visibility = View.GONE
            }
        }

        binding?.progressBar?.visibility = View.GONE
        setLeaveData(leaveRequest)
    }

    private fun setLeaveData(leaveRequest: LeaveRequest?) {
        if(leaveRequest != null) {
            binding?.apply {
                tvFullNameInput.text = leaveRequest.employeeName
                tvUserIdInput.text = leaveRequest.employeeId
                tvSubmittedOnInput.text = leaveRequest.timeStamp
                tvLeaveIdInput.text = leaveRequest.id
                tvLeaveTypeInput.text = leaveRequest.type.toString()
                tvStartDateInput.text = leaveRequest.startDate
                tvEndDateInput.text = leaveRequest.endDate
            }
        }
    }

    private suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean) {
        if (leaveRequest?.companyId != null) {
            viewModel.changeLeaveRequestStatus(leaveRequest, isApproved).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            dismiss()
                            ViewUtils.replaceFragment(
                                requireActivity() as AppCompatActivity,
                                R.id.nav_main_content_container,
                                HomeFragment(),
                                HomeFragment::class.java.simpleName
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

    companion object {
        fun newInstance(leaveRequest: LeaveRequest): LeaveRequestDetailsFragment {
            val fragment = LeaveRequestDetailsFragment()
            val args = Bundle()
            args.putParcelable("leaveRequest", leaveRequest)
            fragment.arguments = args
            return fragment
        }
    }


}