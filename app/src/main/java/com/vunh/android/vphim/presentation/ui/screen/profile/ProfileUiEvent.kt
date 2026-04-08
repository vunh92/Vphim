package com.vunh.android.vphim.presentation.ui.screen.profile

sealed class ProfileUiEvent {
    data class OnPhoneNumberChanged(val value: String) : ProfileUiEvent()
    object OnPhoneLoginClick : ProfileUiEvent()
    object OnLogout : ProfileUiEvent()
}
