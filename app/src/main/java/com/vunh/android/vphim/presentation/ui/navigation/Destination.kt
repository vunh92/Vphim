package com.vunh.android.vphim.presentation.ui.navigation

import com.vunh.android.vphim.R

enum class Destination(
    val label: String,
    val icon: Int,
) {
    HOME("Trang chủ", R.drawable.ic_home),
    SERIES("Phim Bộ", R.drawable.ic_home),
    SINGLE("Phim Lẻ", R.drawable.ic_home),
    ANIME("Hoạt Hình", R.drawable.ic_home),
    FAVORITES("Yêu thích", R.drawable.ic_favorite),
}
