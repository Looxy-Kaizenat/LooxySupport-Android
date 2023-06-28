package com.looxy.looxysupport.data

class DataDashboard {
    data class StatusCheck(
        val status : String,
        val weekUser : String,
        val allUser : String,
        val weekSalons : String,
        val allSalons : String,
        val weekBookings : String,
        val allBookings : String,
        val weekValue : String,
        val allValue : String,
        val weekOffer : String,
        val allOffer : String,
        val moneyRequested : String
    )
}