package com.looxy.looxysupport.data

class DataUserList {
    data class StatusCheck(
        val status : String,
        val response : List<DataList>,
        val message : String
    )

    data class DataList(
        val id : String,
        val name : String,
        val email : String,
        val phone : String,
        val gender : String,
        val booking_count : String
    )
}