package com.looxy.looxysupport.data

class DataSendMoneyToOwner {
    data class StatusCheck(
        val status : String,
        val response : String,
        val message : String
    )
}