package com.looxy.looxysupport.data

class DataIncentiveUserList {
    data class StatusCheck(
        val status : String,
        val response : List<DataList>,
        val message : String
    )

    data class DataList(
        val id : String,
        val name : String,
        val mobile : String,
        val email : String,
        val url : String,
        val shop_name : String
    )
}