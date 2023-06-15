package com.looxy.looxysupport.data

class UserListResponse {
    data class UserList(
        val status : String,
        val response : List<DataUserList>,
        val message : String
    )

    data class DataUserList(
        val id : String,
        val name : String,
        val email : String,
        val phone : String,
        val gender : String,
        val booking_count : String
    )
}