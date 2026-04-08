package com.vunh.android.vphim.presentation.ui.screen.profile

data class ProfileUiState(
    val isLoggedIn: Boolean = false,
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val displayPhoneNumber: String = "",
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val isOtpPopupVisible: Boolean = false,
    val otpCode: String = "",
    val otpError: String? = null,
    val username: String = "",
    val password: String = "",
    val loginError: String? = null,
    val isEditing: Boolean = false,
    val editName: String = "",
    val editPhone: String = "",
    val editNameError: String? = null,
    val editPhoneError: String? = null,
)
