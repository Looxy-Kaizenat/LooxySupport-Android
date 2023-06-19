package com.looxy.looxysupport.utilities

import android.util.Log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    fun getInstance(): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .build()

        return Retrofit.Builder().baseUrl(APICall().baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }

    class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val url = request.url().toString()
            val parameters = request.url().queryParameterNames()

            if(parameters.isNotEmpty())
                Log.e("testing", "Parameters: $parameters")

            val body = request.body()
            if (body is FormBody) {
                Log.e("testing", "Parameters:")
                for (i in 0 until body.size()) {
                    val name = body.encodedName(i)
                    val value = body.encodedValue(i)
                    Log.e("testing", "$name: $value")
                }
            }

            Log.e("testing", "URL: $url")

            return chain.proceed(request)
        }
    }
}