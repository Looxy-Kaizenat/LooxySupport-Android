package com.looxy.looxysupport.utilities

import java.util.Calendar

object CommonUtils {

    fun addTimeFromGivenStartTime(sumOfServiceTime: String, startTime: String): String {
        val startTimes = startTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = startTimes[0].toInt() // Set the start hour to 10
        calendar[Calendar.MINUTE] = startTimes[1].toInt()
        val serviceTimes = sumOfServiceTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        calendar.add(Calendar.HOUR_OF_DAY, serviceTimes[0].toInt())
        calendar.add(Calendar.MINUTE, serviceTimes[1].toInt())
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        return String.format("%02d:%02d", hour, minute)
    }

    fun getMinimumTime(times: ArrayList<String>): String {
        // Step 1: Convert each time in the array to milliseconds since midnight
        val millis = LongArray(times.size)
        for (i in times.indices) {
            val parts = times[i].split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            millis[i] = (hours * 60 * 60 * 1000 + minutes * 60 * 1000).toLong()
        }

        // Step 2: Find the minimum value of the milliseconds array
        var minMillis = Long.MAX_VALUE
        for (m in millis) {
            if (m < minMillis) {
                minMillis = m
            }
        }

        // Step 3: Convert the minimum value back to a formatted time string
        val hours = (minMillis / (60 * 60 * 1000)).toInt()
        val minutes = (minMillis / (60 * 1000) % 60).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }


    fun sumOfServiceTimes(arrayList: ArrayList<String>): String {
        val times: MutableList<Calendar> = ArrayList()
        for (list in arrayList) {
            val strings = list.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val time = Calendar.getInstance()
            time[Calendar.HOUR_OF_DAY] = strings[0].toInt()
            time[Calendar.MINUTE] = strings[1].toInt()
            times.add(time)
        }
        val resultTime = Calendar.getInstance()
        resultTime[Calendar.HOUR_OF_DAY] = 0 // Set the hour to 10
        resultTime[Calendar.MINUTE] = 0
        for (time in times) {
            resultTime.add(Calendar.HOUR_OF_DAY, time[Calendar.HOUR_OF_DAY])
            resultTime.add(Calendar.MINUTE, time[Calendar.MINUTE])
        }
        val resultHour = resultTime[Calendar.HOUR_OF_DAY]
        val resultMinute = resultTime[Calendar.MINUTE]
        return "$resultHour:$resultMinute"
    }
}