package com.naozumi.izinboss.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.naozumi.izinboss.R
import com.naozumi.izinboss.adapter.LeaveListAdapter
import com.naozumi.izinboss.helper.Result
import com.naozumi.izinboss.data.UserPreferences
import com.naozumi.izinboss.databinding.ActivityMainBinding
import com.naozumi.izinboss.databinding.NavHeaderMainBinding
import com.naozumi.izinboss.model.local.LeaveRequest
import com.naozumi.izinboss.model.local.User
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.supportActionBar?.setHomeButtonEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.onboarding_image_1)

        binding.navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this ,binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0))

        toggle.setToolbarNavigationClickListener {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false // Disable this callback to allow the default back behavior
                    onBackPressed() // Trigger the default back behavior
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        lifecycleScope.launch {
            checkIfUserHasCompany(viewModel.getCurrentUser().toString())
        }

        binding.fabAddLeave.setOnClickListener {
            ViewUtils.moveActivity(this@MainActivity, AddLeaveActivity::class.java)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                setupLeaveList()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                viewModel.signOut()
                ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
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

    private suspend fun checkIfUserHasCompany(userId: String) {
        val user: User? = viewModel.getUserData(userId)
        if (user != null) {
            viewModel.saveUser(user)
            if (user.companyId == null) {
                ViewUtils.moveActivityNoHistory(this, CreateCompanyActivity::class.java, userId)
            } else {
                //companyId = user.companyId
            }
        }
    }

    private suspend fun setupLeaveList() {
        val leaveListAdapter = LeaveListAdapter()
        val user = runBlocking { viewModel.getUser().first() }
        binding.rvLeaves.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = leaveListAdapter
        }

        leaveListAdapter.setOnItemClickCallback(object: LeaveListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: LeaveRequest) {
                ViewUtils.moveActivity(
                    this@MainActivity,
                    LeaveDetailsActivity::class.java,
                    user?.companyId
                )
            }
        })

        viewModel.getAllLeaveRequests(user?.companyId.toString()).observe(this) { result ->
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