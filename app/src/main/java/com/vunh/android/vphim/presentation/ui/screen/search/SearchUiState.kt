package com.vunh.android.vphim.presentation.ui.screen.search

import com.vunh.android.vphim.domain.model.Category
import com.vunh.android.vphim.domain.model.Movie

data class SearchUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val categories: List<Category> = emptyList(),
    val countries: List<Category> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val error: String? = null,
    
    // Search
    val searchQuery: String = "",
    val isSearchingByKeyword: Boolean = false,
    
    // Filters
    val selectedTypeList: String = "phim-moi-cap-nhat",
    val selectedSortField: String = "modified.time",
    val selectedSortType: String = "desc",
    val selectedSortLang: String? = null,
    val selectedCategory: String? = null,
    val selectedCountry: String? = null,
    val selectedYear: Int? = null,
    val limit: Int = 20
)
