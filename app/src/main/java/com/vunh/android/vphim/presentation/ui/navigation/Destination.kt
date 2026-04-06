package com.vunh.android.vphim.presentation.ui.navigation

import com.vunh.android.vphim.R

enum class Destination(
    val label: String,
    val icon: Int,
) {
    HOME("Trang chủ", R.drawable.ic_home),
    FAVORITES("Yêu thích", R.drawable.ic_favorite),
//    PROFILE("Cá nhân", R.drawable.ic_account_box),
}
