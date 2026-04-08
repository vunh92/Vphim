package com.vunh.android.vphim.data.repository

import com.vunh.android.vphim.data.local.UserLocalDataSource
import com.vunh.android.vphim.data.remote.api.AuthApiService
import com.vunh.android.vphim.data.remote.dto.LoginRequestDto
import com.vunh.android.vphim.domain.model.User
import com.vunh.android.vphim.domain.repository.UserRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {
    override suspend fun login(username: String, password: String): User {
        try {
            val response = authApiService.login(
                LoginRequestDto(username = username, password = password)
            )
            val user = User(
                id = response.id.toString(),
                phoneNumber = "0123456789",
                username = response.username,
                name = "${response.firstName} ${response.lastName}",
                email = response.email,
                avatarUrl = response.image
            )
            return user
        } catch (e: HttpException) {
            throw Exception(
                if (e.code() == 400 || e.code() == 401) {
                    "Tên đăng nhập hoặc mật khẩu không đúng"
                } else {
                    "Đăng nhập thất bại"
                }
            )
        } catch (e: Exception) {
            throw Exception(e.message ?: "Đăng nhập thất bại")
        }
    }

    override suspend fun saveUser(user: User) {
        userLocalDataSource.saveUser(user)
    }

    override fun getSavedUser(): User? = userLocalDataSource.getUser()

    override suspend fun logout() {
        userLocalDataSource.clearUser()
    }
}
