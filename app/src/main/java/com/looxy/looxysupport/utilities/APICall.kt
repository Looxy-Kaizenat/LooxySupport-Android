package com.looxy.looxysupport.utilities

import com.looxy.looxysupport.data.DataBookingHistory
import com.looxy.looxysupport.data.DataDashboard
import com.looxy.looxysupport.data.DataIncentiveUserList
import com.looxy.looxysupport.data.DataMoneyRequested
import com.looxy.looxysupport.data.DataPostStatus
import com.looxy.looxysupport.data.DataSendMoneyToOwner
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.data.DataUserList
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

class APICall {

//    var baseUrl: String = "https://www.api.looxy.in/v1/api/"
    var baseUrl: String = "https://www.api.looxy.in/dev_api/api/"

    interface ApiUserList {
        @GET("allUsers")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("search_parm") search_parm: String,
                              @Query("from_date") from_date: String,
                              @Query("to_date") to_date: String,
                              @Query("pagination") pagination: String,
                              @Query("page_number") page_number: Int,
                              @Query("page_limit") page_limit: Int) : Response<DataUserList.StatusCheck>
    }

    interface ApiShopList {
        @GET("allSalons")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("need_transaction") need_transaction: String,
                              @Query("search_parm") search_parm: String,
                              @Query("pagination") pagination: String,
                              @Query("page_number") page_number: Int,
                              @Query("page_limit") page_limit: Int) : Response<DataShopList.StatusCheck>
    }

    interface ApiShopDetails {
        @GET("allSalons")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("need_transaction") need_transaction: String,
                              @Query("shop_id") shop_id: String) : Response<DataShopList.StatusCheck>
    }

    interface ApiBookingHistory {
        @GET("allBookings")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("booking_type") booking_type: String,
                              @Query("status") status: String,
                              @Query("services_status") services_status: String,
                              @Query("from_date") from_date: String,
                              @Query("to_date") to_date: String,
                              @Query("search_parm") search_parm: String,
                              @Query("pagination") pagination: String,
                              @Query("page_number") page_number: Int,
                              @Query("page_limit") page_limit: Int) : Response<DataBookingHistory.StatusCheck>
    }

    interface ApiDashBoard {
        @GET("adminDashboard")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("from_date") from_date: String,
                              @Query("to_date") to_date: String) : Response<DataDashboard.StatusCheck>
    }

    interface ApiMoneyRequested {
        @GET("moneyRequestedList")
        suspend fun getResult(@Header("MyToken") token: String) : Response<DataMoneyRequested.StatusCheck>
    }

    interface ApiSendMoneyToOwner {
        @FormUrlEncoded
        @POST("requestMoneyUpdate")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Field("request_id") request_id: String,
                              @Field("payment_mode") payment_mode: String,
                              @Field("requested_amt") requested_amt: String,
                              @Field("ref_number") ref_number: String,
                              @Field("shop_id") shop_id: String,
                              @Field("owner_id") owner_id: String) : Response<DataSendMoneyToOwner.StatusCheck>
    }

    interface ApiIncentiveUserList {
        @GET("incentiveQRList")
        suspend fun getResult(@Header("MyToken") token: String) : Response<DataIncentiveUserList.StatusCheck>
    }

    interface ApiGenerateIncentiveQR {
        @FormUrlEncoded
        @POST("generateIncentiveQR")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Field("name") name: String,
                              @Field("mobile") mobile: String,
                              @Field("email") email: String,
                              @Field("shop_name") shop_name: String) : Response<DataPostStatus.StatusCheck>
    }

    interface ApiAssignReferralCode {
        @FormUrlEncoded
        @POST("assign-ref-code")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Field("shop_id") shop_id: String,
                              @Field("ref_code") ref_code: String) : Response<DataPostStatus.StatusCheck>
    }

    interface ApiSendNotification {
        @FormUrlEncoded
        @POST("bulkNotifications")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Field("user_type") user_type: String,
                              @Field("title") title: String,
                              @Field("message") message: String) : Response<DataPostStatus.StatusCheck>
    }
}