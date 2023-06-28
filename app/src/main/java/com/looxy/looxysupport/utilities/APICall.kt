package com.looxy.looxysupport.utilities

import com.looxy.looxysupport.data.DataBookingHistory
import com.looxy.looxysupport.data.DataDashboard
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.data.DataUserList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class APICall {

//    var baseUrl: String = "https://www.api.looxy.in/api/"
    var baseUrl: String = "https://www.api.looxy.in/dev_api/api/"

    interface ApiUserList {
        @GET("allUsers")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("search_parm") search_parm: String,
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

    /*interface UserListApi {
        @GET("allUsers")
        suspend fun getResult(@Header("MyToken") token: String) : Response<UserListResponse.UserList>
    }*/

    /*interface CheckExpertsShopApi {
        @FormUrlEncoded
        @POST("check_experts_shops")
        suspend fun getResult(@Field("phone") phone: String) : Response<StatusClasses.CheckExpertsShop>
    }

    interface LoginApi {
        @FormUrlEncoded
        @POST("experts_login")
        suspend fun getResult(@Field("phone") phone: String,
                              @Field("password") password: String,
                              @Field("shop_id") shop_id: String,
                              @Field("firebase_token") firebase_token: String) : Response<StatusClasses.Login>
    }*/
}