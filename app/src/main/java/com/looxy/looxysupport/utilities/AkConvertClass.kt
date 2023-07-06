package com.looxy.looxysupport.utilities

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.text.DecimalFormat
import java.util.Hashtable

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