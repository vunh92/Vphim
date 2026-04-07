package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ActionMoviesResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ActionMoviesDataDto
)

data class ActionMoviesDataDto(
    @SerializedName("items") val items: List<ActionMovieDto>,
    @SerializedName("params") val params: ParamsDto,
    @SerializedName("seoOnPage") val seoOnPage: SeoOnPageDto,
    @SerializedName("breadCrumb") val breadCrumb: List<BreadCrumbDto>,
    @SerializedName("titlePage") val titlePage: String,
    @SerializedName("APP_DOMAIN_FRONTEND") val appDomainFrontend: String,
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val appDomainCdnImage: String
)

data class ActionMovieDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    @SerializedName("year") val year: Int
)

data class ParamsDto(
    @SerializedName("type_list") val typeList: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("filterCategory") val filterCategory: List<String>,
    @SerializedName("filterCountry") val filterCountry: List<String>,
    @SerializedName("filterYear") val filterYear: List<String>,
    @SerializedName("filterType") val filterType: List<String>,
    @SerializedName("sortField") val sortField: String,
    @SerializedName("sortType") val sortType: String,
    @SerializedName("pagination") val pagination: PaginationV1Dto
)

data class PaginationV1Dto(
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalItemsPerPage") val totalItemsPerPage: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class SeoOnPageDto(
    @SerializedName("og_type") val ogType: String,
    @SerializedName("titleHead") val titleHead: String,
    @SerializedName("descriptionHead") val descriptionHead: String,
    @SerializedName("og_image") val ogImage: List<String>,
    @SerializedName("updated_at") val updatedAt: Long
)

data class BreadCrumbDto(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String?,
    @SerializedName("isCurrent") val isCurrent: Boolean
)
