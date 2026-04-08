package com.vunh.android.vphim.presentation.ui.screen.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.OnPhoneNumberChanged -> {
                val sanitized = event.value.filter { it.isDigit() }.take(11)
                _uiState.update { state ->
                    state.copy(
                        phoneNumber = sanitized,
                        phoneNumberError = validatePhoneNumber(sanitized, showBlankError = false)
                    )
                }
            }
            ProfileUiEvent.OnPhoneLoginClick -> {
                _uiState.update { state ->
                    state.copy(
                        phoneNumberError = validatePhoneNumber(
                            phoneNumber = state.phoneNumber,
                            showBlankError = true
                        )
                    )
                }
            }
            ProfileUiEvent.OnLogout -> {
                // Handle logout logic
            }
        }
    }

    private fun validatePhoneNumber(
        phoneNumber: String,
        showBlankError: Boolean,
    ): String? {
        if (phoneNumber.isBlank()) {
            return if (showBlankError) "Vui lòng nhập số điện thoại" else null
        }

        return if (phoneNumber.matches(Regex("^0\\d{9,10}$"))) {
            null
        } else {
            "Số điện thoại không hợp lệ"
        }
    }
}
