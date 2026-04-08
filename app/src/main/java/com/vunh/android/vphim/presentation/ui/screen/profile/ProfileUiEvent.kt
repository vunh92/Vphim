package com.vunh.android.vphim.presentation.ui.screen.profile

sealed class ProfileUiEvent {
    data class OnPhoneNumberChanged(val value: String) : ProfileUiEvent()
    object OnPhoneLoginClick : ProfileUiEvent()
    data class OnOtpCodeChanged(val value: String) : ProfileUiEvent()
    object OnOtpVerifyClick : ProfileUiEvent()
    object OnDismissOtpPopup : ProfileUiEvent()
    
    data class OnUsernameChanged(val value: String) : ProfileUiEvent()
    data class OnPasswordChanged(val value: String) : ProfileUiEvent()
    object OnLoginClick : ProfileUiEvent()
    
    object OnEditProfileClick : ProfileUiEvent()
    data class OnEditNameChanged(val value: String) : ProfileUiEvent()
    data class OnEditPhoneChanged(val value: String) : ProfileUiEvent()
    object OnSaveProfileClick : ProfileUiEvent()
    object OnCancelEditClick : ProfileUiEvent()

    object OnLogout : ProfileUiEvent()
}
