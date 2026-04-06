package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LatestMoviesResponseDto(
    @SerializedName("status") val status: Boolean,
    @SerializedName("msg") val message: String,
    @SerializedName("items") val items: List<MovieDto>,
    @SerializedName("pagination") val pagination: PaginationDto,
)

data class MovieDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    @SerializedName("year") val year: Int,
    @SerializedName("modified") val modified: ModifiedDto,
    @SerializedName("imdb") val imdb: ImdbDto,
    @SerializedName("tmdb") val tmdb: TmdbDto,
)

data class ModifiedDto(
    @SerializedName("time") val time: String,
)

data class ImdbDto(
    @SerializedName("id") val id: String?,
)

data class TmdbDto(
    @SerializedName("type") val type: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("season") val season: Int?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
)

data class PaginationDto(
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalItemsPerPage") val totalItemsPerPage: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
)
