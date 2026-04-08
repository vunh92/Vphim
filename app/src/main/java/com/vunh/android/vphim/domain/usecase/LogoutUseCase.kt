package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
