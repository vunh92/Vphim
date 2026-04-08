package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.User
import com.vunh.android.vphim.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(user: User) {
        repository.saveUser(user)
    }
}
