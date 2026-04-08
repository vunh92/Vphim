package com.vunh.android.vphim.presentation.ui.screen.profile

data class ProfileUiState(
    val isLoggedIn: Boolean = false,
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
)
