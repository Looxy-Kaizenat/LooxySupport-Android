package com.looxy.looxysupport.utilities

import com.looxy.looxysupport.data.UserListResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class APICall {

//    var baseUrl: String = "https://www.api.looxy.in/api/"
    var baseUrl: String = "https://www.api.looxy.in/dev_api/api/"

    interface UserListApi {
        @GET("allUsers")
        suspend fun getResult(@Header("MyToken") token: String,
                              @Query("search_parm") search_parm: String,
                              @Query("pagination") pagination: String,
                              @Query("page_number") page_number: Int,
                              @Query("page_limit") page_limit: Int) : Response<UserListResponse.UserList>
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