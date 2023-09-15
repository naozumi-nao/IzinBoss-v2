package com.naozumi.izinboss.model.util

import android.app.AlertDialog
import android.content.Context
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun getCurrentDateAndTime():String{
        val timeDate = SimpleDateFormat(
            "HH:mm dd-MMM-yyyy",
            Locale.getDefault()
        )
        return timeDate.format(Date())
    }

    fun convertLongToDateAndTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat (
            "dd-MMM-yyyy | HH:mm",
            Locale.getDefault()
        )
        return format.format(date)
    }

    fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat (
            "dd-MMM-yyyy",
            Locale.getDefault()
        )
        return format.format(date)
    }

    fun showDateRangePicker(context: Context, fragmentManager: FragmentManager, onDatePicked: (Long, Long) -> Unit) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select date")
            .setSelection(Pair(today,today))
            .build()

        dateRangePicker.show(
            fragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = datePicked.first
            val endDate = datePicked.second

            if(startDate != null && endDate != null && startDate >= today && endDate >= today) {
                onDatePicked(startDate, endDate)
            } else {
                AlertDialog.Builder(context)
                    .setTitle("Invalid Date Selection")
                    .setMessage("Please select dates starting from today.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

}