package com.vunh.android.vphim.data.remote.api

import com.vunh.android.vphim.data.remote.dto.CategoryResponseDto
import com.vunh.android.vphim.data.remote.dto.LatestMoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PhimApiService {

    @GET("danh-sach/phim-moi-cap-nhat")
    suspend fun getLatestMovies(
        @Query("page") page: Int,
    ): LatestMoviesResponseDto

    @GET("the-loai")
    suspend fun getCategories(): CategoryResponseDto
}
