package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.data.Result
import com.naozumi.izinboss.databinding.ActivityAddLeaveBinding
import com.naozumi.izinboss.model.local.Leave
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.AddLeaveViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class AddLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLeaveBinding
    private lateinit var viewModel: AddLeaveViewModel
    //private lateinit var currentPhotoPath: String
    //private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AddLeaveViewModel::class.java]

        binding.progressBar.visibility = View.GONE
        //binding.btnCamera.setOnClickListener { startCamera() }
        //binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnAdd.setOnClickListener {
            lifecycleScope.launch {
                uploadLeave()
            }
        }

        supportActionBar?.title = "Add Leave Request"
    }

    private suspend fun uploadLeave() {
        val description = binding.edAddDescription.text.toString()

        val leave = Leave(note = description)

        viewModel.addLeaveToDatabase(leave).observe(this) { result ->
            when(result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@AddLeaveActivity,
                        "Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    ViewUtils.moveActivityNoHistory(this, MainActivity::class.java)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.error))
                        setMessage(result.error)
                        setPositiveButton(getString(R.string.continue_on)) { _, _ -> }
                        create()
                        show()
                    }
                }
            }
        }
    }

}