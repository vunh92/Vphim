package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.data.remote.dto.MovieDetailResponseDto
import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(slug: String): MovieDetailResponseDto {
        return repository.getMovieDetail(slug)
    }
}
