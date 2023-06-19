package com.looxy.looxysupport.data

class DataShopList {
    data class StatusCheck(
        val status : String,
        val response : List<DataList>,
        val message : String
    )

    data class DataList(
        val shop_id : String,
        val name : String,
        val owner_user_id : String,
        val onwer_name : String,
        val owner_phone : String,
        val shop_phone : String,
        val address : String,
        val latitude : String,
        val longitude : String,
        val available_status : String,
        val status : String,
        val is_gst_applicable : String,
        val gst_per : String,
        val booking_count : String,
        val amount : String,
        val redeem_details : List<RedeemList>,
        val transaction_details : List<TransactionList>
    )

    data class RedeemList(
        val id : String,
        val owner_id : String,
        val shop_id : String,
        val amount : String,
        val requested_on : String,
        val status : String,
        val reject_reason : String,
        val updated_on : String,
        val process_by : String,
        val process_on : String,
        val processed_amt : String,
        val reference_number : String,
        val payment_mode : String
    )

    data class TransactionList(
        val trans_id : String,
        val owner_id : String,
        val shop_id : String,
        val booking_id : String,
        val date : String,
        val particulars : String,
        val amount : String,
        val type : String,
        val closing_balance : String,
        val ref_number : String,
        val added_on : String,
        val updated_on : String
    )
}