package com.vunh.android.vphim.data.remote.api

import com.vunh.android.vphim.data.remote.dto.LoginRequestDto
import com.vunh.android.vphim.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
