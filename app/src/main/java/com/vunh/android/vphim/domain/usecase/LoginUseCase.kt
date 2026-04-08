package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.User
import com.vunh.android.vphim.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(username: String, password: String): User {
        if (username.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Username and password cannot be empty")
        }
        return repository.login(username, password)
    }
}
