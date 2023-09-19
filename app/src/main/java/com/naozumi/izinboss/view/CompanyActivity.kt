package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naozumi.izinboss.databinding.ActivityCompanyBinding
import com.naozumi.izinboss.model.adapter.UserListAdapter
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.viewmodel.CompanyViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CompanyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompanyBinding
    private lateinit var viewModel: CompanyViewModel
    private val userListAdapter = UserListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[CompanyViewModel::class.java]

        lifecycleScope.launch {
            val user = viewModel.getUserData(viewModel.getCurrentUser().toString())
            setCompanyData(user?.companyId.toString())
            getCompanyMembers()
        }

        binding.rvMembersList.apply {
            layoutManager = LinearLayoutManager(this@CompanyActivity)
            setHasFixedSize(true)
            adapter = userListAdapter
        }

        binding.progressBar.visibility = View.GONE
    }

    private suspend fun setCompanyData(companyId: String) {
        val company = viewModel.getCompanyData(companyId)

        if (company != null) {
            binding.apply {
                tvCompanyNameInput.text = company.name
                tvCompanyIdInput.text = company.id
            }
        }
    }

    private suspend fun getCompanyMembers() {
        val user = viewModel.getUserData(viewModel.getCurrentUser().toString())
        val companyId = runBlocking { user?.companyId.toString() }

        userListAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Toast.makeText(
                    this@CompanyActivity,
                    "Clicked on user",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO move to User Profile
            }
        })
        viewModel.getCompanyMembers(companyId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val memberData = result.data
                        Log.d("getCompanyMembers: ", "$memberData")
                        userListAdapter.submitList(memberData)
                        Log.d("currentList: ", "${userListAdapter.currentList}")
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@CompanyActivity,
                            "Error: " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
