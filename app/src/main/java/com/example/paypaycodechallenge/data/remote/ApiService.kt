package com.example.paypaycodechallenge.data.remote

import com.example.paypaycodechallenge.data.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest.json?/outstanding?limit=10")
    suspend fun getLiveRates(@Query("app_id", encoded = true) apiKey: String): Response<APIResponse>
}