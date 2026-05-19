package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        repository.toggleFavorite(movie)
    }
}
