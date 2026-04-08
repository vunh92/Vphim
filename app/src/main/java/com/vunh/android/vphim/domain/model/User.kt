package com.vunh.android.vphim.domain.model

data class User(
    val id: String,
    val phoneNumber: String,
    val username: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)
