package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailResponseDto(
    @SerializedName("status") val status: Boolean,
    @SerializedName("msg") val msg: String,
    @SerializedName("movie") val movie: MovieDetailDto,
    @SerializedName("episodes") val episodes: List<EpisodeDto>
)

data class MovieDetailDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("is_copyright") val isCopyright: Boolean,
    @SerializedName("sub_docquyen") val subDocquyen: Boolean,
    @SerializedName("chieurap") val chieurap: Boolean,
    @SerializedName("trailer_url") val trailerUrl: String,
    @SerializedName("time") val time: String,
    @SerializedName("episode_current") val episodeCurrent: String,
    @SerializedName("episode_total") val episodeTotal: String,
    @SerializedName("quality") val quality: String,
    @SerializedName("lang") val lang: String,
    @SerializedName("notify") val notify: String,
    @SerializedName("showtimes") val showtimes: String,
    @SerializedName("year") val year: Int,
    @SerializedName("view") val view: Int,
    @SerializedName("actor") val actor: List<String>,
    @SerializedName("director") val director: List<String>,
    @SerializedName("category") val category: List<CategoryItemDto>,
    @SerializedName("country") val country: List<CountryDto>
)

data class CategoryItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)

data class CountryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)

data class EpisodeDto(
    @SerializedName("server_name") val serverName: String,
    @SerializedName("server_data") val serverData: List<ServerDataDto>
)

data class ServerDataDto(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("filename") val filename: String,
    @SerializedName("link_embed") val linkEmbed: String,
    @SerializedName("link_m3u8") val linkM3u8: String
)
