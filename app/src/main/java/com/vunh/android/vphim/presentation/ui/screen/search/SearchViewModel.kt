package com.vunh.android.vphim.presentation.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.usecase.GetCategoriesUseCase
import com.vunh.android.vphim.domain.usecase.GetCountriesUseCase
import com.vunh.android.vphim.domain.usecase.GetMoviesByTypeListUseCase
import com.vunh.android.vphim.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMoviesByTypeListUseCase: GetMoviesByTypeListUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadFilters()
        fetchMovies()
    }

    private fun loadFilters() {
        viewModelScope.launch {
            try {
                val categories = getCategoriesUseCase()
                val countries = getCountriesUseCase()
                _uiState.update { it.copy(categories = categories, countries = countries) }
            } catch (e: Exception) {
                // Silently handle or log error
            }
        }
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.Refresh -> {
                _uiState.update { it.copy(currentPage = 1, movies = emptyList(), error = null, searchQuery = "", isSearchingByKeyword = false) }
                fetchMovies()
            }
            is SearchUiEvent.LoadNextPage -> {
                if (_uiState.value.currentPage < _uiState.value.totalPages && !_uiState.value.isLoading) {
                    _uiState.update { it.copy(currentPage = it.currentPage + 1) }
                    if (_uiState.value.isSearchingByKeyword) {
                        searchByKeyword(_uiState.value.searchQuery, isLoadMore = true)
                    } else {
                        fetchMovies(isLoadMore = true)
                    }
                }
            }
            is SearchUiEvent.SearchByKeyword -> {
                _uiState.update { it.copy(currentPage = 1, movies = emptyList(), error = null, searchQuery = event.keyword, isSearchingByKeyword = true) }
                searchByKeyword(event.keyword)
            }
            is SearchUiEvent.UpdateSearchQuery -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is SearchUiEvent.ChangeTypeList -> {
                _uiState.update { it.copy(selectedTypeList = event.typeList) }
            }
            is SearchUiEvent.ChangeSortField -> {
                _uiState.update { it.copy(selectedSortField = event.sortField) }
            }
            is SearchUiEvent.ChangeSortType -> {
                _uiState.update { it.copy(selectedSortType = event.sortType) }
            }
            is SearchUiEvent.ChangeSortLang -> {
                _uiState.update { it.copy(selectedSortLang = event.sortLang) }
            }
            is SearchUiEvent.ChangeCategory -> {
                _uiState.update { it.copy(selectedCategory = event.category) }
            }
            is SearchUiEvent.ChangeCountry -> {
                _uiState.update { it.copy(selectedCountry = event.country) }
            }
            is SearchUiEvent.ChangeYear -> {
                _uiState.update { it.copy(selectedYear = event.year) }
            }
            is SearchUiEvent.ChangeLimit -> {
                _uiState.update { it.copy(limit = event.limit) }
            }
            is SearchUiEvent.ApplyFilters -> {
                _uiState.update { it.copy(currentPage = 1, movies = emptyList(), isSearchingByKeyword = false) }
                fetchMovies()
            }
        }
    }

    private fun fetchMovies(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val response = getMoviesByTypeListUseCase(
                    typeList = state.selectedTypeList,
                    page = state.currentPage,
                    sortField = state.selectedSortField,
                    sortType = state.selectedSortType,
                    sortLang = state.selectedSortLang,
                    category = state.selectedCategory,
                    country = state.selectedCountry,
                    year = state.selectedYear,
                    limit = state.limit,
                    keyword = if (state.searchQuery.isBlank()) null else state.searchQuery
                )
                
                val domainMovies = response.data.items.map { dto ->
                    Movie(
                        id = dto.id,
                        title = dto.name,
                        originTitle = dto.originName,
                        slug = dto.slug,
                        posterUrl = response.data.appDomainCdnImage + "/" + dto.posterUrl,
                        thumbUrl = response.data.appDomainCdnImage + "/" + dto.thumbUrl,
                        year = dto.year,
                        modifiedTime = "",
                        imdbId = null,
                        type = null
                    )
                }

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        movies = if (isLoadMore) it.movies + domainMovies else domainMovies,
                        totalPages = response.data.params.pagination.totalPages
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private fun searchByKeyword(keyword: String, isLoadMore: Boolean = false) {
        if (keyword.isBlank()) {
            _uiState.update { it.copy(isSearchingByKeyword = false) }
            fetchMovies()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val response = searchMoviesUseCase(
                    keyword = keyword,
                    page = state.currentPage,
                    limit = state.limit
                )
                
                val domainMovies = response.data.items.map { dto ->
                    Movie(
                        id = dto.id,
                        title = dto.name,
                        originTitle = dto.originName,
                        slug = dto.slug,
                        posterUrl = response.data.appDomainCdnImage + "/" + dto.posterUrl,
                        thumbUrl = response.data.appDomainCdnImage + "/" + dto.thumbUrl,
                        year = dto.year,
                        modifiedTime = "",
                        imdbId = null,
                        type = null
                    )
                }

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        movies = if (isLoadMore) it.movies + domainMovies else domainMovies,
                        totalPages = response.data.params.pagination.totalPages
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
