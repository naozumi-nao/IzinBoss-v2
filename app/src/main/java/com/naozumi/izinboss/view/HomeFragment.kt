package com.naozumi.izinboss.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: MainViewModel
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

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding?.progressBar?.visibility = View.GONE

        lifecycleScope.launch {
            user = viewModel.getUser().first()
            if(user?.companyId.isNullOrBlank()) {
                binding?.animEmptyList?.playAnimation()
                binding?.swipeToRefresh?.visibility = View.GONE
                binding?.fabAddLeave?.visibility = View.GONE
            } else {
                setupLeaveList()
                binding?.swipeToRefresh?.setOnRefreshListener {
                    lifecycleScope.launch {
                        setupLeaveList()
                    }
                }
            }
            binding?.fabAddLeave?.setOnClickListener(1000L) {
                ViewUtils.moveActivity(requireActivity(), RequestLeaveActivity::class.java)
            }
        }
    }

    private suspend fun checkIfUserHasCompany(userId: String): Boolean {
        val user: User? = viewModel.getUserData(userId)
        if (user != null) {
            viewModel.saveUser(user)
            if (user.companyId != null) {
                return true
            }
        }
        return false
    }

    private suspend fun setupLeaveList() {
        val leaveListAdapter = LeaveListAdapter()
        val user = runBlocking { viewModel.getUser().first() }
        binding?.rvLeaves?.apply {
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

        viewModel.getAllLeaveRequests(user?.companyId.toString()).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        val leaveData = result.data
                        if (leaveData.isEmpty()) {
                            binding?.animEmptyList?.playAnimation()
                            binding?.swipeToRefresh?.isRefreshing = false
                        } else {
                            binding?.animEmptyList?.visibility = View.GONE
                            leaveListAdapter.submitList(leaveData)
                            binding?.swipeToRefresh?.isRefreshing = false
                        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}