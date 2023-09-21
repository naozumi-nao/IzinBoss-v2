package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        lifecycleScope.launch {
            user = viewModel.getUser().first()
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
                    if (user?.companyId.isNullOrEmpty()) {
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

        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_settings -> {
                    ViewUtils.moveActivity(this@MainActivity, SettingsActivity::class.java)
                    true
                }
                else -> false
            }
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
                    if (user?.companyId.isNullOrEmpty()) {
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
                    viewModel.signOut()
                    viewModel.deleteCurrentUserDataStore()
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
        if (viewModel.getCurrentUser().isNullOrEmpty()) {
            viewModel.signOut()
            viewModel.deleteCurrentUserDataStore()
            ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
        }

        lifecycleScope.launch {
            setUserData(viewModel.getCurrentUser().toString())
        }
    }

    private suspend fun setUserData(userId: String) {
        val headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0))
        val user: User? = viewModel.getUserData(userId)
        if (user != null) {
            headerBinding.apply {
                tvFullName.text = user.name
                tvEmail.text = user.email
                Glide.with(this@MainActivity)
                    .load(user.profilePicture)
                    .error(R.drawable.baseline_person_24)
                    .into(ivProfilePhoto)
            }
        }
    }
}