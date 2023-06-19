package com.looxy.looxysupport.utilities

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
object DateFormat {
    
    fun dateWithTimeToDate(deadline: String): String {
        var date = Date()
        val calendarFormat =
            SimpleDateFormat("yyyy-MM-dd H:mm:ss")
        try {
            date = calendarFormat.parse(deadline)!!!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("dd MMM yyyy")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateWithTimeToTime(deadline: String): String {
        var date = Date()
        val calendarFormat =
            SimpleDateFormat("yyyy-MM-dd H:mm:ss")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("h:mm a")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateWithTimeToDateWithTime(deadline: String): String {
        var date = Date()
        val calendarFormat =
            SimpleDateFormat("yyyy-MM-dd H:mm:ss")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat =
            SimpleDateFormat("dd MMM yyyy, h:mm a")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateWithTimeToDateWithTimePipeFormat(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd H:mm:ss")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("dd MMM yyyy")
        val timeFormat = SimpleDateFormat("h:mm a")
        val cal = Calendar.getInstance()
        cal.time = date
        val strdate = dayFormat.format(cal.time)
        val strtime = timeFormat.format(cal.time).uppercase(Locale.getDefault())
        return "$strdate | $strtime"
    }

    fun dateToDate(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("dd MMM yyyy")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateToDateWithDay(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat =
            SimpleDateFormat("EEEE, dd MMM yyyy")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateToOnlyDate(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("dd")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateToMonthYearDay(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("MMM yyyy, EEEE")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun time24to12hoursFormat(time: String): String {
        var finaltime = ""
        try {
            val sdf = SimpleDateFormat("H:mm")
            val dateObj = sdf.parse(time)
            finaltime = SimpleDateFormat("hh:mm a").format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return finaltime.uppercase(Locale.getDefault())
    }

    fun time12to24hoursFormat(time: String): String {
        var finaltime = ""
        try {
            val sdf = SimpleDateFormat("hh:mm a")
            val dateObj = sdf.parse(time)
            finaltime = SimpleDateFormat("H:mm").format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return finaltime.uppercase(Locale.getDefault())
    }

    fun dateWithBulkTimeToDate(deadline: String): String {
        var date = Date()
        val calendarFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("dd MMM yyyy")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateWithBulkTimeToDateWithDay(deadline: String): String {
        var date = Date()
        val calendarFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("E dd MMM yyyy")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateWithBulkTimeToDateWithTime(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("dd MMM yyyy")
        val timeFormat = SimpleDateFormat("h:mm a")
        val cal = Calendar.getInstance()
        cal.time = date
        val strdate = dayFormat.format(cal.time)
        val strtime = timeFormat.format(cal.time).uppercase(Locale.getDefault())
        return "$strdate | $strtime"
    }

    fun dateToDateWithBulkTime(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun dateWithTimeToDateWithBulkTime(deadline: String): String {
        var date = Date()
        val calendarFormat =
            SimpleDateFormat("yyyy-MM-dd H:mm")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun monthYearToMonth(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("MMM-yyyy")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("MMM")
        val cal = Calendar.getInstance()
        cal.time = date
        return dayFormat.format(cal.time)
    }

    fun monthYearToQuarter(deadline: String): String {
        var date = Date()
        val calendarFormat = SimpleDateFormat("MMM-yyyy")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("MMM")
        val cal = Calendar.getInstance()
        cal.time = date
        return "Q" + (cal[Calendar.MONTH] / 3 + 1)
    }

    fun compareToCurrentDate(deadline: String): Int {
        var i = 0
        var date = Date()
        val calendarFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try {
            date = calendarFormat.parse(deadline)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (Date() > date) {
//            before
            i = -1
        } else if (Date() < date) {
//            after
            i = 1
        } else if (Date().compareTo(date) == 0) {
//            equal
            i = 0
        }
        return i
    }
}