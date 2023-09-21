package com.naozumi.izinboss.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.FragmentLeaveRequestDetailsBinding
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.StringUtils
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import com.naozumi.izinboss.viewmodel.company.CompanyViewModel
import kotlinx.coroutines.launch

class LeaveRequestDetailsFragment : DialogFragment() {
    private var _binding: FragmentLeaveRequestDetailsBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: CompanyViewModel
    private var leaveRequest: LeaveRequest? = null
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val leaveRequestBundle =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable("leaveRequest", LeaveRequest::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelable("leaveRequestKey")
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


    }

    private fun setLeaveData(user: User?) {
        binding?.apply {

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