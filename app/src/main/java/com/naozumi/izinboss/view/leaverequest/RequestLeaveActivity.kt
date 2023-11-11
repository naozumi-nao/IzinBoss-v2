package com.naozumi.izinboss.view.leaverequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.databinding.ActivityRequestLeaveBinding
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.util.FormValidator
import com.naozumi.izinboss.model.util.TimeUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.view.MainActivity
import com.naozumi.izinboss.viewmodel.RequestLeaveViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestLeaveBinding
    private val viewModel by viewModels<RequestLeaveViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        val leaveTypeList: List<String> = LeaveRequest.Type.values().map { type ->
            type.name.lowercase().replaceFirstChar { it.uppercase() }
        }
        val typeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, leaveTypeList)
        binding.actvAddType.setAdapter(typeAdapter)

        setupDateRangePicker()
        val textWatcher = FormValidator.createTextWatcherWithButton(
            binding.btnRequestLeave,
            binding.actvAddType,
            binding.edStartDateInput,
            binding.edEndDateInput,
            binding.edAddReason
        )
        binding.btnRequestLeave.isEnabled = false
        binding.actvAddType.addTextChangedListener(textWatcher)
        binding.edStartDateInput.addTextChangedListener(textWatcher)
        binding.edEndDateInput.addTextChangedListener(textWatcher)
        binding.edAddReason.addTextChangedListener(textWatcher)

        binding.btnRequestLeave.setOnClickListener(2000L) {
            lifecycleScope.launch(Dispatchers.Main) {
                uploadLeave()
            }
        }

        supportActionBar?.title = "Add Leave Request"
    }

    private fun uploadLeave() {
        val user = viewModel.user
        val timeStamp = TimeUtils.getCurrentDateAndTime()
        val typeString = binding.actvAddType.text.toString().trim()
        val typeMap = mapOf(
            "Sick" to LeaveRequest.Type.SICK,
            "Vacation" to LeaveRequest.Type.VACATION,
            "Personal" to LeaveRequest.Type.PERSONAL
        )
        val type = typeMap[typeString]
        val startDate = binding.edStartDateInput.text.toString().trim()
        val endDate = binding.edEndDateInput.text.toString().trim()
        val description = binding.edAddReason.text.toString().trim()

        val leaveRequest = LeaveRequest(
            employeeId = user?.uid,
            employeeName = user?.name,
            createdAt = timeStamp,
            startDate = startDate,
            endDate = endDate,
            reason = description,
            type = type ?: LeaveRequest.Type.SICK
        )
        viewModel.addLeaveRequest(user?.companyId.toString(), leaveRequest)
            .observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@RequestLeaveActivity,
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

    private fun setupDateRangePicker() {
        val dateRangePickerCallback: (Long, Long) -> Unit = { startDate, endDate ->
            binding.edStartDateInput.text = Editable.Factory.getInstance()
                .newEditable(TimeUtils.convertLongToDate(startDate))
            binding.edEndDateInput.text =
                Editable.Factory.getInstance().newEditable(TimeUtils.convertLongToDate(endDate))
        }

        binding.edStartDateInput.setOnClickListener(3000L) {
            showDateRangePicker(dateRangePickerCallback)
        }

        binding.edEndDateInput.setOnClickListener(3000L) {
            showDateRangePicker(dateRangePickerCallback)
        }

        binding.tilStartDate.setOnClickListener(3000L) {
            showDateRangePicker(dateRangePickerCallback)
        }

        binding.tilEndDate.setOnClickListener(3000L) {
            showDateRangePicker(dateRangePickerCallback)
        }
    }

    private fun showDateRangePicker(callback: (Long, Long) -> Unit) {
        TimeUtils.showMaterialDateRangePicker(supportFragmentManager, callback)
    }
}