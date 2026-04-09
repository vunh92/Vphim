package com.vunh.android.vphim.data.remote.api

import com.vunh.android.vphim.data.remote.dto.ActionMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.AnimeMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.CategoryDto
import com.vunh.android.vphim.data.remote.dto.LatestMoviesResponseDto
import com.vunh.android.vphim.data.remote.dto.MovieDetailResponseDto
import com.vunh.android.vphim.data.remote.dto.SearchCountryDto
import com.vunh.android.vphim.data.remote.dto.SearchMoviesResponseDto
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

    @GET("quoc-gia")
    suspend fun getCountries(): List<SearchCountryDto>

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

    @GET("phim/{slug}")
    suspend fun getMovieDetail(
        @Path("slug") slug: String
    ): MovieDetailResponseDto

    @GET("v1/api/tim-kiem")
    suspend fun searchMovies(
        @Query("keyword") keyword: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type_list") typeList: String? = null,
        @Query("sort_field") sortField: String? = null,
        @Query("sort_type") sortType: String? = null,
        @Query("sort_lang") sortLang: String? = null,
        @Query("category") category: String? = null,
        @Query("country") country: String? = null,
        @Query("year") year: Int? = null
    ): SearchMoviesResponseDto
}
