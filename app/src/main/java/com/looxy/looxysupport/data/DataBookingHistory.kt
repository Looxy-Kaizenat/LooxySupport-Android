package com.looxy.looxysupport.data

import android.app.Service

class DataBookingHistory {
    data class StatusCheck(
        val status : String,
        val booking_data : List<DataList>,
        val message : String
    )

    data class DataList(
        val booking_id : String,
        val booking_date : String,
        val shop_id : String,
        val shop_name : String,
        val shop_logo : String,
        val net_price : String,
        val final_price : String,
        val offers_amount : String,
        val payment_mode : String,
        val booking_number : String,
        val shop_phone : String,
        val is_gst_applicable : String,
        val gst_per : String,
        val tax : String,
        val name : String,
        val phone : String,
        val email : String,
        val user_img : String,
        val service_data : List<ServiceList>,
        val receipt_data : List<ReceiptData>
    )

    data class ServiceList(
        val booking_transaction_id : String,
        val start_time : String,
        val end_time : String,
        val time_duration : String,
        val services_completed : String,
        val service_name : String,
        val experts_name : String,
        val service_final_price : String,
        val service_setting_id : String
    )

    data class ReceiptData(
        val booking_receipt_id : String,
        val booking_id : String,
        val booking_transaction_id : String,
        val receipt_number : String,
        val path : String
    )
}