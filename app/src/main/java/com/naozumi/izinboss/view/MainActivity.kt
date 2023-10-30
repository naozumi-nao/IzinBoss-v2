package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityMainBinding
import com.naozumi.izinboss.databinding.NavHeaderMainBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.company.CompanyProfileFragment
import com.naozumi.izinboss.view.company.CreateCompanyFragment
import com.naozumi.izinboss.view.entry.LoginActivity
import com.naozumi.izinboss.view.user.UserProfileFragment
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.userData.observe(this) { user ->
            viewModel.saveUserToPreferences(user)
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.bottom_nav_home -> {
                    ViewUtils.replaceFragment(this,
                        R.id.nav_main_content_container,
                        HomeFragment(),
                        HomeFragment::class.java.simpleName
                    )
                    true
                }
                R.id.bottom_nav_company -> {
                    if (viewModel.user?.companyId.isNullOrEmpty()) {
                        ViewUtils.replaceFragment(this,
                            R.id.nav_main_content_container,
                            CreateCompanyFragment(),
                            CreateCompanyFragment::class.java.simpleName
                        )
                    } else {
                        ViewUtils.replaceFragment(this,
                            R.id.nav_main_content_container,
                            CompanyProfileFragment(),
                            CompanyProfileFragment::class.java.simpleName
                        )
                    }
                    true
                }
                R.id.bottom_nav_profile -> {
                    ViewUtils.replaceFragment(this,
                        R.id.nav_main_content_container,
                        UserProfileFragment(),
                        UserProfileFragment::class.java.simpleName
                    )
                    true
                }
                else -> false
            }
        }

        binding.appBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    ViewUtils.replaceFragment(this,
                        R.id.nav_main_content_container,
                        HomeFragment(),
                        HomeFragment::class.java.simpleName
                    )
                }
                R.id.nav_profile -> {
                    ViewUtils.replaceFragment(this,
                        R.id.nav_main_content_container,
                        UserProfileFragment(),
                        UserProfileFragment::class.java.simpleName
                    )
                }
                R.id.nav_company -> {
                    if (viewModel.user?.companyId.isNullOrEmpty()) {
                        ViewUtils.replaceFragment(this,
                            R.id.nav_main_content_container,
                            CreateCompanyFragment(),
                            CreateCompanyFragment::class.java.simpleName
                        )
                    } else {
                        ViewUtils.replaceFragment(this,
                            R.id.nav_main_content_container,
                            CompanyProfileFragment(),
                            CompanyProfileFragment::class.java.simpleName
                        )
                    }
                }
                R.id.nav_logout -> {
                    logout()
                    ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val toggle = ActionBarDrawerToggle(this ,binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toggle.setToolbarNavigationClickListener {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_main_content_container, HomeFragment()).commit()
            binding.navigationView.setCheckedItem(R.id.nav_home)
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

        //DEBUG FOR STUCK LOGINS
        if (viewModel.getCurrentUser().isBlank()) {
            viewModel.signOut()
            viewModel.deleteCurrentUserPref()
            ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
        }

        setUserData()
    }

    private fun setUserData() {
        val headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0))
        headerBinding.apply {
            tvFullName.text = viewModel.user?.name
            tvEmail.text = viewModel.user?.email
            Glide.with(this@MainActivity)
                .load(viewModel.user?.profilePicture)
                .error(R.drawable.baseline_person_24)
                .into(ivProfilePhoto)
        }
    }

    private fun logout() {
        viewModel.signOut()
        viewModel.deleteCurrentUserPref()
    }
}