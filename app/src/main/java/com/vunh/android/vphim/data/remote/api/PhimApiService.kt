package com.vunh.android.vphim.data.remote.api

import com.vunh.android.vphim.data.remote.dto.ActionMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.AnimeMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.CategoryDto
import com.vunh.android.vphim.data.remote.dto.LatestMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.SeriesMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.SingleMoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhimApiService {

    @GET("danh-sach/phim-moi-cap-nhat")
    suspend fun getLatestMovies(
        @Query("page") page: Int,
    ): LatestMoviesResponseDto

    @GET("the-loai")
    suspend fun getCategories(): List<CategoryDto>

    @GET("v1/api/the-loai/{categorySlug}")
    suspend fun getMoviesByCategory(
        @Path("categorySlug") categorySlug: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ActionMoviesResponseDto

    @GET("v1/api/danh-sach/phim-bo")
    suspend fun getSeriesMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): SeriesMoviesResponseDto

    @GET("v1/api/danh-sach/phim-le")
    suspend fun getSingleMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): SingleMoviesResponseDto

    @GET("v1/api/danh-sach/hoat-hinh")
    suspend fun getAnimeMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): AnimeMoviesResponseDto
}
