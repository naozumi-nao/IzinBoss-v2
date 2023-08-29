package com.naozumi.izinboss.view

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.data.Result
import com.naozumi.izinboss.databinding.ActivityAddLeaveBinding
import com.naozumi.izinboss.model.local.Leave
import com.naozumi.izinboss.util.CameraUtils
import com.naozumi.izinboss.util.CameraUtils.uriToFile
import com.naozumi.izinboss.util.GenericUtils
import com.naozumi.izinboss.util.ViewUtils
import com.naozumi.izinboss.viewmodel.AddLeaveViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class AddLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLeaveBinding
    private lateinit var viewModel: AddLeaveViewModel
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AddLeaveViewModel::class.java]

        binding.progressBar.visibility = View.GONE
        binding.btnPickDate.setOnClickListener{
            GenericUtils.showDateRangePicker(this, supportFragmentManager) { startDate, endDate ->
                binding.tvStartDateInput.text = GenericUtils.convertLongToDate(startDate)
                binding.tvEndDateInput.text = GenericUtils.convertLongToDate(endDate)
            }
        }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnAdd.setOnClickListener {
            lifecycleScope.launch {
                uploadLeave()
            }
        }
        supportActionBar?.title = "Add Leave Request"
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                CameraUtils.rotateFile(file)
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddLeaveActivity)
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if(!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permissions_error),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private suspend fun uploadLeave() {

        val timeStamp = GenericUtils.getCurrentDateAndTime()
        val description = binding.edAddDescription.text.toString().trim()
        val startDate = binding.tvStartDateInput.text.toString().trim()
        val endDate = binding.tvEndDateInput.text.toString().trim()

        val leave = Leave(timeStamp = timeStamp, startDate = startDate, endDate = endDate, reason = description)

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


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        CameraUtils.createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.naozumi.izinboss",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_PICK
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}