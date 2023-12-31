package com.naozumi.izinboss.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naozumi.izinboss.model.adapter.LeaveListAdapter
import com.naozumi.izinboss.databinding.FragmentHomeBinding
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.leaverequest.LeaveRequestDetailsFragment
import com.naozumi.izinboss.view.leaverequest.RequestLeaveActivity
import com.naozumi.izinboss.view.leaverequest.RequestLeaveFragment
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = viewModel.user
        binding?.progressBar?.visibility = View.GONE

        if(user?.companyId.isNullOrBlank()) { // if user doesn't have company
            binding?.animEmptyList?.playAnimation()
            binding?.tvNoLeaveRequests?.visibility = View.VISIBLE
            binding?.swipeToRefresh?.visibility = View.GONE
            binding?.fabAddLeaveRequest?.visibility = View.GONE
        } else {
            if (user?.role == User.UserRole.MANAGER) {
                binding?.fabAddLeaveRequest?.visibility = View.GONE
            }
            setupLeaveList()
            binding?.swipeToRefresh?.setOnRefreshListener {
                setupLeaveList()
            }
        }
        binding?.fabAddLeaveRequest?.setOnClickListener(1000L) {
            ViewUtils.moveActivity(requireActivity(), RequestLeaveActivity::class.java)
            //RequestLeaveFragment().show(parentFragmentManager, "leaveRequest")
        }
    }

    private fun setupLeaveList() {
        val leaveListAdapter = LeaveListAdapter()
        binding?.rvLeaveRequests?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = leaveListAdapter
        }

        leaveListAdapter.setOnItemClickCallback(object: LeaveListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: LeaveRequest) {
                val detailsFragment = LeaveRequestDetailsFragment.newInstance(data)
                detailsFragment.show(parentFragmentManager, "leaveRequest")
            }
        })
        viewModel.getAllLeaveRequests().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                        binding?.tvNoLeaveRequests?.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        val leaveData = result.data
                        if (leaveData.isEmpty()) {
                            binding?.animEmptyList?.playAnimation()
                            binding?.tvNoLeaveRequests?.visibility = View.VISIBLE
                            binding?.swipeToRefresh?.isRefreshing = false
                        } else {
                            binding?.animEmptyList?.visibility = View.GONE
                            binding?.tvNoLeaveRequests?.visibility = View.GONE
                            leaveListAdapter.submitList(leaveData)
                            binding?.swipeToRefresh?.isRefreshing = false
                        }
                    }
                    is Result.Error -> {
                        binding?.progressBar?.visibility = View.GONE
                        binding?.tvNoLeaveRequests?.visibility = View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}