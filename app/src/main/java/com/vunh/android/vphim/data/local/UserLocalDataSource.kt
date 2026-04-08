package com.vunh.android.vphim.data.local

import android.content.Context
import com.vunh.android.vphim.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        preferences.edit()
            .putString(KEY_ID, user.id)
            .putString(KEY_PHONE_NUMBER, user.phoneNumber)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_AVATAR_URL, user.avatarUrl)
            .apply()
    }

    fun getUser(): User? {
        val id = preferences.getString(KEY_ID, null) ?: return null
        return User(
            id = id,
            phoneNumber = preferences.getString(KEY_PHONE_NUMBER, "") ?: "",
            username = preferences.getString(KEY_USERNAME, "") ?: "",
            name = preferences.getString(KEY_NAME, "") ?: "",
            email = preferences.getString(KEY_EMAIL, "") ?: "",
            avatarUrl = preferences.getString(KEY_AVATAR_URL, null)
        )
    }

    fun clearUser() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREF_NAME = "user_prefs"
        const val KEY_ID = "user_id"
        const val KEY_PHONE_NUMBER = "user_phone_number"
        const val KEY_USERNAME = "user_username"
        const val KEY_NAME = "user_name"
        const val KEY_EMAIL = "user_email"
        const val KEY_AVATAR_URL = "user_avatar_url"
    }
}
