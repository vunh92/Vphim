package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.User
import com.vunh.android.vphim.domain.repository.UserRepository
import javax.inject.Inject

class GetSavedUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): User? = repository.getSavedUser()
}
