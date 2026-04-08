package com.vunh.android.vphim.data.local

import com.vunh.android.vphim.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileManager @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
) {
    private val _currentUser = MutableStateFlow(userLocalDataSource.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun setUser(user: User) {
        userLocalDataSource.saveUser(user)
        _currentUser.value = user
    }

    fun clearUser() {
        userLocalDataSource.clearUser()
        _currentUser.value = null
    }

    fun getCurrentUser(): User? = _currentUser.value
}
