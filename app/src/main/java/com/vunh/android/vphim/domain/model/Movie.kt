package com.vunh.android.vphim.domain.model

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val isFavorite: Boolean = false
)
