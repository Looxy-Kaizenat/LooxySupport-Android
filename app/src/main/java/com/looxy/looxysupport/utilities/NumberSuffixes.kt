package com.looxy.looxysupport.utilities

object NumberSuffixes {
    operator fun get(number: String): String {
        return if (number.endsWith("1") && !number.endsWith("11")) "st" else if (number.endsWith("2") && !number.endsWith(
                "12"
            )
        ) "nd" else if (number.endsWith("3") && !number.endsWith("13")) "rd" else "th"
    }
}