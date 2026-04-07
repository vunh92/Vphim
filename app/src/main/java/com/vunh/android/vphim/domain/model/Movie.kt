package com.vunh.android.vphim.domain.model

import java.io.Serializable

data class Movie(
    val id: String,
    val title: String,
    val originTitle: String,
    val slug: String,
    val posterUrl: String,
    val thumbUrl: String,
    val year: Int,
    val modifiedTime: String,
    val imdbId: String?,
    val type: String?,
    val isFavorite: Boolean = false
) : Serializable
