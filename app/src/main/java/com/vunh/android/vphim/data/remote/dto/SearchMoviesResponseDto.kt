package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchMoviesResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SearchMoviesDataDto
)

data class SearchMoviesDataDto(
    @SerializedName("items") val items: List<SearchMovieDto>,
    @SerializedName("params") val params: SearchParamsDto,
    @SerializedName("titlePage") val titlePage: String,
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val appDomainCdnImage: String
)

data class SearchMovieDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    @SerializedName("year") val year: Int
)

data class SearchParamsDto(
    @SerializedName("type_list") val typeList: String,
    @SerializedName("filterCategory") val filterCategory: List<String>,
    @SerializedName("filterCountry") val filterCountry: List<String>,
    @SerializedName("filterYear") val filterYear: List<String>,
    @SerializedName("sortField") val sortField: String,
    @SerializedName("sortType") val sortType: String,
    @SerializedName("pagination") val pagination: PaginationV1Dto
)
