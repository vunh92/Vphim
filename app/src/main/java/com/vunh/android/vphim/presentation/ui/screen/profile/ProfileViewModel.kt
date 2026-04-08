package com.vunh.android.vphim.presentation.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.data.local.ProfileManager
import com.vunh.android.vphim.domain.model.User
import com.vunh.android.vphim.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val profileManager: ProfileManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            profileManager.currentUser.collectLatest { user ->
                if (user != null) {
                    applyLoggedInUser(user)
                } else if (_uiState.value.isLoggedIn) {
                    _uiState.update { ProfileUiState() }
                }
            }
        }
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.OnPhoneNumberChanged -> {
                val sanitized = event.value.filter { it.isDigit() }.take(10)
                _uiState.update { it.copy(phoneNumber = sanitized, phoneNumberError = null) }
            }
            ProfileUiEvent.OnPhoneLoginClick -> {
                val error = validatePhoneNumber(_uiState.value.phoneNumber)
                if (error == null) {
                    _uiState.update { it.copy(isOtpPopupVisible = true, phoneNumberError = null) }
                } else {
                    _uiState.update { it.copy(phoneNumberError = error) }
                }
            }
            is ProfileUiEvent.OnOtpCodeChanged -> {
                val sanitized = event.value.filter { it.isDigit() }.take(6)
                _uiState.update { it.copy(otpCode = sanitized, otpError = null) }
            }
            ProfileUiEvent.OnOtpVerifyClick -> verifyOtp()
            ProfileUiEvent.OnDismissOtpPopup -> {
                _uiState.update { it.copy(isOtpPopupVisible = false, otpCode = "", otpError = null) }
            }
            is ProfileUiEvent.OnUsernameChanged -> {
                _uiState.update { it.copy(username = event.value, loginError = null) }
            }
            is ProfileUiEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.value, loginError = null) }
            }
            ProfileUiEvent.OnLoginClick -> login()
            
            // Edit Profile
            ProfileUiEvent.OnEditProfileClick -> {
                _uiState.update { it.copy(
                    isEditing = true,
                    editName = it.name,
                    editPhone = it.displayPhoneNumber,
                    editNameError = null,
                    editPhoneError = null
                ) }
            }
            is ProfileUiEvent.OnEditNameChanged -> {
                _uiState.update {
                    it.copy(
                        editName = event.value,
                        editNameError = null
                    )
                }
            }
            is ProfileUiEvent.OnEditPhoneChanged -> {
                val sanitized = event.value.filter { it.isDigit() }.take(10)
                _uiState.update {
                    it.copy(
                        editPhone = sanitized,
                        editPhoneError = null
                    )
                }
            }
            ProfileUiEvent.OnSaveProfileClick -> saveProfile()
            ProfileUiEvent.OnCancelEditClick -> {
                _uiState.update {
                    it.copy(
                        isEditing = false,
                        editNameError = null,
                        editPhoneError = null
                    )
                }
            }
            
            ProfileUiEvent.OnLogout -> {
                profileManager.clearUser()
            }
        }
    }

    private fun login() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            try {
                val user = loginUseCase(currentState.username, currentState.password)
                profileManager.setUser(user)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, loginError = e.message ?: "Đăng nhập thất bại") }
            }
        }
    }

    private fun verifyOtp() {
        val code = _uiState.value.otpCode
        if (code.length < 6) {
            _uiState.update { it.copy(otpError = "Vui lòng nhập đủ 6 chữ số") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500)
            val user = User(
                id = _uiState.value.phoneNumber,
                phoneNumber = _uiState.value.phoneNumber,
                username = _uiState.value.phoneNumber,
                name = "Người dùng Vphim",
                email = "user@vphim.com",
                avatarUrl = "https://i.pravatar.cc/300?u=\$0"
            )
            profileManager.setUser(user)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOtpPopupVisible = false,
                    otpCode = "",
                    otpError = null
                )
            }
        }
    }

    private fun saveProfile() {
        val state = _uiState.value
        val editNameError = validateName(state.editName)
        val editPhoneError = validatePhoneNumber(state.editPhone)

        if (editNameError != null || editPhoneError != null) {
            _uiState.update {
                it.copy(
                    editNameError = editNameError,
                    editPhoneError = editPhoneError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate saving
            delay(500)
            val updatedUser = User(
                id = state.username, // keep id same
                phoneNumber = state.editPhone,
                username = state.username,
                name = state.editName,
                email = state.email,
                avatarUrl = state.avatarUrl
            )
            profileManager.setUser(updatedUser)
            _uiState.update { it.copy(
                isLoading = false,
                isEditing = false,
                editNameError = null,
                editPhoneError = null
            ) }
        }
    }

    private fun applyLoggedInUser(user: User, isOtpPopupVisible: Boolean = false) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isLoggedIn = true,
                name = user.name,
                email = user.email,
                avatarUrl = user.avatarUrl,
                displayPhoneNumber = user.phoneNumber,
                username = user.username,
                isOtpPopupVisible = isOtpPopupVisible
            )
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): String? {
        if (phoneNumber.isBlank()) return "Vui lòng nhập số điện thoại"
        val vnPhoneRegex = Regex("^0(3|5|7|8|9)\\d{8}$")
        return if (vnPhoneRegex.matches(phoneNumber)) null else "Số điện thoại Việt Nam không hợp lệ"
    }

    private fun validateName(name: String): String? {
        return if (name.isBlank()) "Vui lòng nhập tên người dùng" else null
    }
}
