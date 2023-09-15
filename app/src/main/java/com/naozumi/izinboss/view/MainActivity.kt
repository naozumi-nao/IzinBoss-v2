package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityMainBinding
import com.naozumi.izinboss.databinding.NavHeaderMainBinding
import com.naozumi.izinboss.model.datamodel.User
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.entry.LoginActivity
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.supportActionBar?.setHomeButtonEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navigationView.setNavigationItemSelectedListener(this)
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
                .replace(R.id.nav_host_fragment_content_main, HomeFragment()).commit()
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

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        //DEBUG FOR STUCK LOGINS
        if (viewModel.getCurrentUser().toString() == "") {
            viewModel.signOut()
            viewModel.deleteCurrentUserDataStore()
            ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
        }

        lifecycleScope.launch {
            setUserData(viewModel.getCurrentUser().toString())
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                ViewUtils.replaceFragment(this,
                    R.id.nav_host_fragment_content_main,
                    HomeFragment(),
                    HomeFragment::class.java.simpleName,
                    getString(R.string.home)
                )
            }
            R.id.nav_profile -> {
                ViewUtils.replaceFragment(this,
                    R.id.nav_host_fragment_content_main,
                    ProfileFragment(),
                    ProfileFragment::class.java.simpleName,
                    getString(R.string.profile)
                )
            }
            R.id.nav_company -> {
                ViewUtils.moveActivity(this, CompanyActivity::class.java)
                /*ViewUtils.replaceFragment(this,
                    R.id.nav_host_fragment_content_main,
                    CompanyFragment(),
                    CompanyFragment::class.java.simpleName,
                    getString(R.string.company)
                )
                 */
            }
            R.id.nav_logout -> {
                viewModel.signOut()
                viewModel.deleteCurrentUserDataStore()
                ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
            R.id.action_settings -> {
                ViewUtils.moveActivity(this@MainActivity, SettingsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                    .error(R.drawable.onboarding_image_1)
                    .into(ivProfilePhoto)
            }
        }
    }
}