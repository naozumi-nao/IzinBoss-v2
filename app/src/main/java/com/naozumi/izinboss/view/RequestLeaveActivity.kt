package com.naozumi.izinboss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.setOnClickListener
import com.naozumi.izinboss.databinding.ActivityRequestLeaveBinding
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.util.TextInputUtils
import com.naozumi.izinboss.model.util.TimeUtils
import com.naozumi.izinboss.model.util.ViewUtils
import com.naozumi.izinboss.viewmodel.AddLeaveViewModel
import com.naozumi.izinboss.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RequestLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestLeaveBinding
    private lateinit var viewModel: AddLeaveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AddLeaveViewModel::class.java]

        binding.progressBar.visibility = View.GONE

        val leaveTypeList: List<String> = LeaveRequest.Type.values().map { type ->
            type.name.lowercase().replaceFirstChar { it.uppercase() }
        }
        val typeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, leaveTypeList)
        binding.actvAddType.setAdapter(typeAdapter)

        binding.edStartDateInput.setOnClickListener(2000L) {
            TimeUtils.showDateRangePicker(this, supportFragmentManager) { startDate, endDate ->
                binding.edStartDateInput.text = Editable.Factory.getInstance()
                    .newEditable(TimeUtils.convertLongToDate(startDate))
                binding.edEndDateInput.text =
                    Editable.Factory.getInstance().newEditable(TimeUtils.convertLongToDate(endDate))
            }
        }
        binding.edEndDateInput.setOnClickListener(2000L) {
            TimeUtils.showDateRangePicker(this, supportFragmentManager) { startDate, endDate ->
                binding.edStartDateInput.text = Editable.Factory.getInstance()
                    .newEditable(TimeUtils.convertLongToDate(startDate))
                binding.edEndDateInput.text =
                    Editable.Factory.getInstance().newEditable(TimeUtils.convertLongToDate(endDate))
            }
        }

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding.btnRequestLeave,
            binding.actvAddType,
            binding.edStartDateInput,
            binding.edEndDateInput,
            binding.edAddDescription
        )

        binding.btnRequestLeave.isEnabled = false
        binding.actvAddType.addTextChangedListener(textWatcher)
        binding.edStartDateInput.addTextChangedListener(textWatcher)
        binding.edEndDateInput.addTextChangedListener(textWatcher)
        binding.edAddDescription.addTextChangedListener(textWatcher)
        binding.btnRequestLeave.setOnClickListener(2000L) {
            lifecycleScope.launch {
                uploadLeave()
            }
        }
        supportActionBar?.title = "Add Leave Request"
    }

    private suspend fun uploadLeave() {
        val user = viewModel.getUser().first()
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
        val description = binding.edAddDescription.text.toString().trim()

        val leaveRequest = LeaveRequest(
            employeeId = user?.uid,
            employeeName = user?.name,
            timeStamp = timeStamp,
            startDate = startDate,
            endDate = endDate,
            reason = description,
            type = type ?: LeaveRequest.Type.SICK
        )
        viewModel.addLeaveRequestToDatabase(user?.companyId.toString(), leaveRequest)
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

    /*
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            // Check if all input fields are filled
            val type = binding.actvAddType.text.toString()
            val dateStart = binding.edStartDateInput.text.toString()
            val dateEnd = binding.edEndDateInput.text.toString()
            val description = binding.edAddDescription.text.toString()
            val isFormFilled = type.isNotEmpty() && dateStart.isNotEmpty() && dateEnd.isNotEmpty() && description.isNotEmpty()

            // Enable or disable the submit button based on form validity
            binding.btnRequestLeave.isEnabled = isFormFilled
        }
    }

     */
}