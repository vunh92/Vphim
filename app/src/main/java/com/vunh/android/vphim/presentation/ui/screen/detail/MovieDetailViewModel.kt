package com.vunh.android.vphim.presentation.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import com.vunh.android.vphim.domain.usecase.GetMovieDetailUseCase
import com.vunh.android.vphim.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    private val _movieId = MutableStateFlow<String?>(null)
    private var movieSlug: String? = null
    private var currentMovie: Movie? = null

    init {
        // Reactively observe favorite status for the current movieId
        _movieId.flatMapLatest { id ->
            if (id != null) {
                movieRepository.isFavorite(id)
            } else {
                flowOf(false)
            }
        }.onEach { isFavorite ->
            _uiState.update { it.copy(isFavorite = isFavorite) }
        }.launchIn(viewModelScope)
    }

    fun setInitialMovieData(movie: Movie) {
        if (movieSlug == movie.slug) return
        this.currentMovie = movie
        _movieId.value = movie.id
        loadMovieDetail(movie.slug)
    }

    private fun loadMovieDetail(slug: String) {
        movieSlug = slug
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val detail = getMovieDetailUseCase(slug)
                _movieId.value = detail.movie.id
                
                // Update currentMovie with more details if needed, 
                // but keep the essential fields for saving to favorites
                if (currentMovie == null || currentMovie?.id != detail.movie.id) {
                    currentMovie = Movie(
                        id = detail.movie.id,
                        title = detail.movie.name,
                        originTitle = detail.movie.originName,
                        slug = detail.movie.slug,
                        posterUrl = detail.movie.posterUrl,
                        thumbUrl = detail.movie.thumbUrl,
                        year = detail.movie.year,
                        modifiedTime = "",
                        imdbId = null,
                        type = detail.movie.type
                    )
                }

                _uiState.update { it.copy(
                    isLoading = false,
                    movieDetail = detail
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Không thể tải thông tin phim"
                ) }
            }
        }
    }

    fun toggleFavorite() {
        val movie = currentMovie ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(movie)
        }
    }
}
