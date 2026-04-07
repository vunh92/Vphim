package com.vunh.android.vphim.presentation.ui.navigation

import com.vunh.android.vphim.R

enum class Destination(
    val route: String,
    val label: String,
    val icon: Int,
) {
    HOME("home", "Trang chủ", R.drawable.ic_home),
    SERIES("series", "Phim Bộ", R.drawable.ic_series),
    SINGLE("single", "Phim Lẻ", R.drawable.ic_single),
    ANIME("anime", "Hoạt Hình", R.drawable.ic_anime),
    FAVORITES("favorites", "Yêu thích", R.drawable.ic_favorite),
}
