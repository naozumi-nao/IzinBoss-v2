package com.naozumi.izinboss.view.leaverequest

import androidx.fragment.app.DialogFragment

class RequestLeaveFragment : DialogFragment() {}
    /*

    private var _binding: FragmentRequestLeaveBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<RequestLeaveViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestLeaveBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.progressBar?.visibility = View.GONE

        val leaveTypeList: List<String> = LeaveRequest.Type.values().map { type ->
            type.name.lowercase().replaceFirstChar { it.uppercase() }
        }
        val typeAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, leaveTypeList)
        binding?.actvAddType?.setAdapter(typeAdapter)

        setupDateRangePicker()

        val textWatcher = TextInputUtils.createTextWatcherWithButton(
            binding?.btnRequestLeave,
            binding?.actvAddType,
            binding?.edStartDateInput,
            binding?.edEndDateInput,
            binding?.edAddReason
        )

        binding?.btnRequestLeave?.isEnabled = false
        binding?.actvAddType?.addTextChangedListener(textWatcher)
        binding?.edStartDateInput?.addTextChangedListener(textWatcher)
        binding?.edEndDateInput?.addTextChangedListener(textWatcher)
        binding?.edAddReason?.addTextChangedListener(textWatcher)

        binding?.btnRequestLeave?.setOnClickListener(2000L) {
            viewLifecycleOwner.lifecycleScope.launch {
                uploadLeave()
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Add Leave Request"
    }

    private fun uploadLeave() {
        val user = viewModel.user
        val timeStamp = TimeUtils.getCurrentDateAndTime()
        val typeString = binding?.actvAddType?.text.toString().trim()
        val typeMap = mapOf(
            "Sick" to LeaveRequest.Type.SICK,
            "Vacation" to LeaveRequest.Type.VACATION,
            "Personal" to LeaveRequest.Type.PERSONAL
        )
        val type = typeMap[typeString]
        val startDate = binding?.edStartDateInput?.text.toString().trim()
        val endDate = binding?.edEndDateInput?.text.toString().trim()
        val description = binding?.edAddReason?.text.toString().trim()

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
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            requireActivity(),
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                        ViewUtils.moveActivityNoHistory(requireActivity(), MainActivity::class.java)
                    }

                    is Result.Error -> {
                        binding?.progressBar?.visibility = View.GONE
                        AlertDialog.Builder(requireActivity()).apply {
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
            binding?.edStartDateInput?.text = Editable.Factory.getInstance()
                .newEditable(TimeUtils.convertLongToDate(startDate))
            binding?.edEndDateInput?.text =
                Editable.Factory.getInstance().newEditable(TimeUtils.convertLongToDate(endDate))
        }

        binding?.edStartDateInput?.setOnClickListener(1500L) {
            showDateRangePicker(dateRangePickerCallback)
        }

        binding?.edEndDateInput?.setOnClickListener(1500L) {
            showDateRangePicker(dateRangePickerCallback)
        }

        binding?.tilStartDate?.setOnClickListener(1500L) {
            showDateRangePicker(dateRangePickerCallback)
        }

        binding?.tilEndDate?.setOnClickListener(1500L) {
            showDateRangePicker(dateRangePickerCallback)
        }
    }

    private fun showDateRangePicker(callback: (Long, Long) -> Unit) {
        TimeUtils.showMaterialDateRangePicker(childFragmentManager, callback)
    }
}

 */
