package com.naozumi.izinboss.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naozumi.izinboss.databinding.FragmentMembersListBinding
import com.naozumi.izinboss.model.adapter.UserListAdapter
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.viewmodel.MembersListViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MembersListFragment : Fragment() {
    private var _binding: FragmentMembersListBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: MembersListViewModel
    private var tabName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMembersListBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.progressBar?.visibility = View.GONE

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MembersListViewModel::class.java]

        tabName = arguments?.getString(ARG_TAB)

        lifecycleScope.launch {
            getCompanyMembers()
        }
    }

    private suspend fun getCompanyMembers() {
        val user = viewModel.getUserData(viewModel.getCurrentUser().toString())
        val companyId = user?.companyId
        val userListAdapter = UserListAdapter()
        binding?.rvMembersList?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = userListAdapter
        }
        userListAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Toast.makeText(
                    context,
                    "Clicked on user",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO move to User Profile
            }
        })

        if (tabName == TAB_EMPLOYEES) {
            viewModel.getCompanyMembers(companyId.toString(), User.UserRole.EMPLOYEE).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            val memberData = result.data
                            userListAdapter.submitList(memberData)
                        }
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Error: " + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else if (tabName == TAB_MANAGERS) {
            viewModel.getCompanyMembers(companyId.toString(), User.UserRole.MANAGER).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            val memberData = result.data
                            userListAdapter.submitList(memberData)
                        }
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Error: " + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.root?.requestLayout() //refresh layout when switching tabs
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARG_TAB = "tab_name"
        const val TAB_EMPLOYEES = "Employees"
        const val TAB_MANAGERS = "Managers"
    }
}