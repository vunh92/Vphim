package com.vunh.android.vphim.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vunh.android.vphim.domain.model.Movie
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class MovieLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getFavoriteMovies(): List<Movie> {
        val json = preferences.getString(KEY_FAVORITES_DATA, null) ?: return emptyList()
        val type = object : TypeToken<List<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    fun toggleFavorite(movie: Movie) {
        val currentFavorites = getFavoriteMovies().toMutableList()
        val index = currentFavorites.indexOfFirst { it.id == movie.id }
        
        if (index != -1) {
            currentFavorites.removeAt(index)
        } else {
            currentFavorites.add(movie.copy(isFavorite = true))
        }
        
        val json = gson.toJson(currentFavorites)
        preferences.edit { putString(KEY_FAVORITES_DATA, json) }
        
        // Also keep the IDs set for quick lookup if needed, 
        // but we can just use the list.
        val ids = currentFavorites.map { it.id }.toSet()
        preferences.edit { putStringSet(KEY_FAVORITE_IDS, ids) }
    }

    fun getFavoriteIds(): Set<String> {
        return preferences.getStringSet(KEY_FAVORITE_IDS, emptySet()) ?: emptySet()
    }

    private companion object {
        const val PREF_NAME = "movie_prefs"
        const val KEY_FAVORITE_IDS = "favorite_movie_ids"
        const val KEY_FAVORITES_DATA = "favorite_movies_data"
    }
}
