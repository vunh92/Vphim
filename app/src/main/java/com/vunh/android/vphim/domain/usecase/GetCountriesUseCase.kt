package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.Category
import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): List<Category> {
        return repository.getCountries()
    }
}
