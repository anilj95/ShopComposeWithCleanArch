package com.example.shopapplication.service

import com.example.shopapplication.repository.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface BingApiService {
    @GET
    suspend fun sendImpression(@Url url: String): NetworkResponse<GenericResponseDto>
}