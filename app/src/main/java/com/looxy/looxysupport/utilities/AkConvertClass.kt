package com.looxy.looxysupport.utilities

import java.text.DecimalFormat

object AkConvertClass {
    fun decimalFormat(amount: String): String {
        val decimalFormat = DecimalFormat("0")
        val value = amount.toDouble()
        return decimalFormat.format(value)
    }
}