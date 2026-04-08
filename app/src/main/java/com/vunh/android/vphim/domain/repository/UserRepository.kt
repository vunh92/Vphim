package com.vunh.android.vphim.domain.repository

import com.vunh.android.vphim.domain.model.User

interface UserRepository {
    suspend fun login(username: String, password: String): User
    suspend fun saveUser(user: User)
    fun getSavedUser(): User?
    suspend fun logout()
}
