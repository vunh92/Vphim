package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class RefreshMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(page: Int = 1) {
        repository.refreshMovies(page = page)
    }
}
