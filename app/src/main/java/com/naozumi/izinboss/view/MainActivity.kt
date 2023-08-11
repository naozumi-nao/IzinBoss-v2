package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.naozumi.izinboss.R
import com.naozumi.izinboss.databinding.ActivityMainBinding
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.MainViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_menu -> {
                viewModel.signOut()
                ViewUtils.moveActivityNoHistory(this@MainActivity, LoginActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}