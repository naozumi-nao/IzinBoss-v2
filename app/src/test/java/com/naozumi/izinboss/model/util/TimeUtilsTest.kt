package com.naozumi.izinboss.model.util

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeUtilsTest {
    @Test
    fun testGetCurrentDateAndTime() {
        val expectedFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault())
        val expectedDateAndTime = expectedFormat.format(Date())
        val currentDateAndTime = TimeUtils.getCurrentDateAndTime()
        assertEquals(expectedDateAndTime, currentDateAndTime)
    }

    @Test
    fun testConvertLongToDate() {
        val timestamp = 1695996022000
        val expectedDate = "29-09-2023"
        val convertedDate = TimeUtils.convertLongToDate(timestamp)
        assertEquals(expectedDate, convertedDate)
    }
}