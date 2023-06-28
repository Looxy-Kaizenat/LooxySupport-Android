package com.looxy.looxysupport.utilities

import java.text.DecimalFormat

object AkConvertClass {
    fun decimalFormat1Digit2Decimal(amount: String): String {
        val decimalFormat = DecimalFormat("0.00")
        val value = amount.toDouble()
        return decimalFormat.format(value)
    }
    fun decimalFormat2Digit2Decimal(amount: String): String {
        val decimalFormat = DecimalFormat("00.00")
        val value = amount.toDouble()
        return decimalFormat.format(value)
    }
    fun decimalFormat1Digit(amount: String): String {
        val decimalFormat = DecimalFormat("0")
        val value = amount.toDouble()
        return decimalFormat.format(value)
    }
    fun decimalFormat2Digit(amount: String): String {
        val decimalFormat = DecimalFormat("00")
        val value = amount.toDouble()
        return decimalFormat.format(value)
    }
}