package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.data.remote.dto.SearchMoviesResponseDto
import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesByTypeListUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        typeList: String,
        page: Int,
        sortField: String?,
        sortType: String?,
        sortLang: String?,
        category: String?,
        country: String?,
        year: Int?,
        limit: Int,
        keyword: String? = null
    ): SearchMoviesResponseDto {
        return repository.searchMovies(
            keyword = keyword,
            page = page,
            limit = limit,
            typeList = typeList,
            sortField = sortField,
            sortType = sortType,
            sortLang = sortLang,
            category = category,
            country = country,
            year = year
        )
    }
}
