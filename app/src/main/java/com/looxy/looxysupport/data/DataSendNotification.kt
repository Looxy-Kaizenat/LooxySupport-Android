package com.looxy.looxysupport.data

class DataSendNotification {
    data class StatusCheck(
        val status : String,
        val response : String,
        val message : String
    )
}