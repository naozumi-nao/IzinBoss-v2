package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naozumi.izinboss.R
import com.naozumi.izinboss.adapter.LeaveListAdapter
import com.naozumi.izinboss.data.Result
import com.naozumi.izinboss.databinding.ActivityMainBinding
import com.naozumi.izinboss.model.local.Leave
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        binding.fabAddLeave.setOnClickListener {
            ViewUtils.moveActivity(this, AddLeaveActivity::class.java)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                setupLeaveList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            setupLeaveList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile_menu -> {
                ViewUtils.moveActivity(this@MainActivity, ProfileActivity::class.java)
                true
            }
            R.id.settings_menu -> {
                ViewUtils.moveActivity(this@MainActivity, SettingsActivity::class.java)
                true
            }
            R.id.logout_menu -> {
                viewModel.signOut()
                ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun setupLeaveList() {
        val leaveListAdapter = LeaveListAdapter()
        binding.rvLeaves.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = leaveListAdapter
        }

        leaveListAdapter.setOnItemClickCallback(object: LeaveListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Leave) {
                ViewUtils.moveActivity(
                    this@MainActivity,
                    LeaveDetailsActivity::class.java,
                    data.id
                )
            }
        })

        viewModel.getAllLeaves().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val leaveData = result.data
                        leaveListAdapter.submitList(leaveData)
                        binding.swipeToRefresh.isRefreshing = false
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Error: " + result.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}