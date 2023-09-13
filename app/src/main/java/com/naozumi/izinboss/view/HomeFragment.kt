package com.naozumi.izinboss.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naozumi.izinboss.R
import com.naozumi.izinboss.core.adapter.LeaveListAdapter
import com.naozumi.izinboss.databinding.FragmentHomeBinding
import com.naozumi.izinboss.core.helper.Result
import com.naozumi.izinboss.core.helper.setOnClickListener
import com.naozumi.izinboss.core.model.local.LeaveRequest
import com.naozumi.izinboss.core.model.local.User
import com.naozumi.izinboss.core.util.ViewUtils
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: MainViewModel

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

        if( runBlocking {
                !checkIfUserHasCompany(viewModel.getCurrentUser().toString()) }
            ) {
            ViewUtils.replaceFragment(requireActivity() as AppCompatActivity,
                R.id.nav_host_fragment_content_main,
                CreateCompanyFragment(),
                CreateCompanyFragment::class.java.simpleName,
                getString(R.string.company)
            )
        } else {
            lifecycleScope.launch {
                setupLeaveList()
            }

            binding?.swipeToRefresh?.setOnRefreshListener {
                lifecycleScope.launch {
                    setupLeaveList()
                }
            }

            binding?.fabAddLeave?.setOnClickListener(1000L) {
                ViewUtils.moveActivity(requireActivity(), AddLeaveActivity::class.java)
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
                ViewUtils.moveActivity(
                    requireActivity(),
                    LeaveDetailsActivity::class.java,
                    user?.companyId
                )
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
                        leaveListAdapter.submitList(leaveData)
                        binding?.swipeToRefresh?.isRefreshing = false
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