package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.data.remote.dto.SearchMoviesResponseDto
import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        keyword: String,
        page: Int,
        limit: Int
    ): SearchMoviesResponseDto {
        return repository.searchMovies(
            keyword,
            page,
            limit
        )
    }
}
