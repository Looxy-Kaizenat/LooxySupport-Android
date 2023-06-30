package com.looxy.looxysupport.utilities

import java.text.DecimalFormat

object AkConvertClass {
    fun decimalFormat1Digit2Decimal(amount: String): String {
        val decimalFormat = DecimalFormat("0.00")
        val value = if (amount != "null" && amount.isNotEmpty()) amount.toDouble() else 0
        return decimalFormat.format(value)
    }
    fun decimalFormat2Digit2Decimal(amount: String): String {
        val decimalFormat = DecimalFormat("00.00")
        val value = if (amount != "null" && amount.isNotEmpty()) amount.toDouble() else 0
        return decimalFormat.format(value)
    }
    fun decimalFormat1Digit(amount: String): String {
        val decimalFormat = DecimalFormat("0")
        val value = if (amount != "null" && amount.isNotEmpty()) amount.toDouble() else 0
        return decimalFormat.format(value)
    }
    fun decimalFormat2Digit(amount: String): String {
        val decimalFormat = DecimalFormat("00")
        val value = if (amount != "null" && amount.isNotEmpty()) amount.toDouble() else 0
        return decimalFormat.format(value)
    }
}