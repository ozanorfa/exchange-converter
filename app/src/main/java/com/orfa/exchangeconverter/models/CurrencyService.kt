package com.orfa.exchangeconverter.models

import com.orfa.exchangeconverter.data.CurrencyResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyService {

    @GET("latest/{currency}")
    suspend fun getCurrencies(@Path("currency") currency: String) : Response<CurrencyResponse>


    companion object {
        var retrofitService: CurrencyService? = null
        fun getInstance() : CurrencyService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(CurrencyService::class.java)
            }
            return retrofitService!!
        }

        const val API_KEY = "61bb0f21e2274b5cac80f21a"
        const val BASE_URL = "https://v6.exchangerate-api.com/v6/${API_KEY}/"

    }
}