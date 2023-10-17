package com.naozumi.izinboss.model.util

import android.app.DatePickerDialog
import android.content.Context
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object TimeUtils {
    fun getCurrentDateAndTime():String{
        val timeDate = SimpleDateFormat(
            "dd-MM-yyyy, HH:mm",
            Locale.getDefault()
        )
        return timeDate.format(Date())
    }

    fun convertLongToDateAndTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat (
            "dd-MM-yyyy, HH:mm",
            Locale.getDefault()
        )
        return format.format(date)
    }

    fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat (
            "dd-MM-yyyy",
            Locale.getDefault()
        )
        return format.format(date)
    }

    fun showMaterialDateRangePicker(fragmentManager: FragmentManager, onDatePicked: (Long, Long) -> Unit) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        val oneYearForward = today + TimeUnit.DAYS.toMillis(365)

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(today)
                .setEnd(oneYearForward)
                .setValidator(DateValidatorPointForward.now())

        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select dates")
            .setSelection(Pair(today,today))
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        dateRangePicker.show(
            fragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = datePicked.first
            val endDate = datePicked.second
            onDatePicked(startDate, endDate)
        }
    }

    fun showDatePicker(context: Context, onDatePicked: (Long) -> Unit) {
        val today = Calendar.getInstance()
        val oneYearForward = Calendar.getInstance()
        oneYearForward.add(Calendar.YEAR, 1) // Add 1 year to the current date

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                val startDate = calendar.timeInMillis

                onDatePicked(startDate)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to today
        datePickerDialog.datePicker.minDate = today.timeInMillis

        // Set the maximum date to one year forward
        datePickerDialog.datePicker.maxDate = oneYearForward.timeInMillis

        datePickerDialog.show()
    }

}