package com.naozumi.izinboss.view.leaverequest

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentLeaveRequestDetailsBinding
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.HomeFragment
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import com.naozumi.izinboss.viewmodel.leavereq.LeaveRequestDetailsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LeaveRequestDetailsFragment : DialogFragment() {
    private var _binding: FragmentLeaveRequestDetailsBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: LeaveRequestDetailsViewModel
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
        viewModel = ViewModelProvider(this, factory)[LeaveRequestDetailsViewModel::class.java]
        lifecycleScope.launch {
            user = viewModel.getUser().first()
        }

        binding?.btnDeleteLeaveRequest?.visibility = View.GONE

        if (user?.role == User.UserRole.MANAGER) {
            binding?.btnApprove?.setOnClickListener(3000L) {
                lifecycleScope.launch {
                    changeLeaveRequestStatus(leaveRequest, true, user?.name.toString())
                }
            }
            binding?.btnReject?.setOnClickListener(3000L) {
                lifecycleScope.launch {
                    changeLeaveRequestStatus(leaveRequest, false, user?.name.toString())
                }
            }
        } else {
            binding?.btnApprove?.visibility = View.GONE
            binding?.btnReject?.visibility = View.GONE
            binding?.btnDeleteLeaveRequest?.visibility = View.GONE
        }

        if (user?.uid == leaveRequest?.employeeId) {
            //binding?.btnDeleteLeaveRequest?.visibility = View.VISIBLE
            binding?.btnDeleteLeaveRequest?.setOnClickListener(3000L) {
                lifecycleScope.launch {
                    deleteLeaveRequest(leaveRequest)
                }
            }
        }

        when(leaveRequest?.status) {
            LeaveRequest.Status.APPROVED -> {
                binding?.apply {
                    btnApprovalStatus.text = requireActivity().getString(R.string.approved)
                    btnApprovalStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green))
                    btnApprovalStatus.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.background_green))
                    tvReviewedBy.text = "Approved By"
                    tvReviewedOn.text = "Approved On"
                    tvReviewedByInput.text = leaveRequest?.reviewedBy
                    tvReviewedOnInput.text = leaveRequest?.reviewedOn
                }
            }
            LeaveRequest.Status.PENDING-> {
                binding?.apply {
                    btnApprovalStatus.text = requireActivity().getString(R.string.pending)
                    btnApprovalStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.yellowish_orange))
                    btnApprovalStatus.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.background_yellow))
                    divider.visibility = View.GONE
                    tvReviewedBy.visibility = View.GONE
                    tvReviewedOn.visibility = View.GONE
                    tvReviewedByInput.visibility = View.GONE
                    tvReviewedOnInput.visibility = View.GONE
                }
            }
            LeaveRequest.Status.REJECTED -> {
                binding?.apply {
                    btnApprovalStatus.text = requireActivity().getString(R.string.rejected)
                    btnApprovalStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red))
                    btnApprovalStatus.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.background_red))
                    tvReviewedBy.text = "Rejected By"
                    tvReviewedOn.text = "Rejected On"
                    tvReviewedByInput.text = leaveRequest?.reviewedBy
                    tvReviewedOnInput.text = leaveRequest?.reviewedOn
                }
            }
            else -> { }
        }

        binding?.progressBar?.visibility = View.GONE
        setLeaveData(leaveRequest)
    }

    private fun setLeaveData(leaveRequest: LeaveRequest?) {
        if(leaveRequest != null) {
            binding?.apply {
                tvFullNameInput.text = leaveRequest.employeeName
                tvUserIdInput.text = leaveRequest.employeeId
                tvSubmittedOnInput.text = leaveRequest.createdAt
                tvLeaveIdInput.text = leaveRequest.id
                tvLeaveTypeInput.text = leaveRequest.type.toString()
                tvStartDateInput.text = leaveRequest.startDate
                tvEndDateInput.text = leaveRequest.endDate
                tvReasonInput.text = leaveRequest.reason
            }
        }
    }

    private suspend fun changeLeaveRequestStatus(leaveRequest: LeaveRequest?, isApproved: Boolean, managerName: String) {
        if (leaveRequest?.companyId != null) {
            viewModel.changeLeaveRequestStatus(leaveRequest, isApproved, managerName).observe(viewLifecycleOwner) { result ->
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

    private suspend fun deleteLeaveRequest(leaveRequest: LeaveRequest?) {
        viewModel.deleteLeaveRequest(leaveRequest).observe(viewLifecycleOwner) { result ->
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