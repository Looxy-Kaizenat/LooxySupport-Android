package com.looxy.looxysupport.data

class DataMoneyRequested {
    data class StatusCheck(
        val status : String,
        val moneyRequested : List<DataList>,
        val message : String
    )

    data class DataList(
        val id : String,
        val owner_id : String,
        val shop_id : String,
        val amount : String,
        val reference_number : String,
        val shop_name : String
    )
}