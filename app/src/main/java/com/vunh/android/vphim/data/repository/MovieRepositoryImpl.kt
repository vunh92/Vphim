package com.vunh.android.vphim.data.repository

import com.vunh.android.vphim.data.local.MovieLocalDataSource
import com.vunh.android.vphim.data.mapper.toDomain
import com.vunh.android.vphim.data.remote.api.PhimApiService
import com.vunh.android.vphim.data.remote.dto.MovieDetailResponseDto
import com.vunh.android.vphim.data.remote.dto.SearchMoviesResponseDto
import com.vunh.android.vphim.domain.model.Category
import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val phimApiService: PhimApiService,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {
    private val remoteMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val favoriteMovieIds = MutableStateFlow(movieLocalDataSource.getFavoriteIds())

    override fun getMovies(): Flow<List<Movie>> {
        return combine(remoteMovies, favoriteMovieIds) { movies, favoriteIds ->
            movies.map { movie ->
                movie.copy(isFavorite = movie.id in favoriteIds)
            }
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return favoriteMovieIds.map { ids ->
            movieLocalDataSource.getFavoriteMovies()
                .filter { it.id in ids }
                .map { it.copy(isFavorite = true) }
        }
    }

    override fun isFavorite(movieId: String): Flow<Boolean> {
        return favoriteMovieIds.map { it.contains(movieId) }
    }

    override suspend fun refreshMovies(page: Int) {
        val response = phimApiService.getLatestMovies(page = page)
        remoteMovies.value = response.items.map { movieDto ->
            movieDto.toDomain()
        }
    }

    override suspend fun toggleFavorite(movie: Movie) {
        movieLocalDataSource.toggleFavorite(movie)
        favoriteMovieIds.value = movieLocalDataSource.getFavoriteIds()
    }

    override suspend fun getCategories(): List<Category> {
        val response = phimApiService.getCategories()
        return response.map { it.toDomain() }
    }

    override suspend fun getCountries(): List<Category> {
        val response = phimApiService.getCountries()
        return response.map { dto ->
            Category(id = dto.id, name = dto.name, slug = dto.slug)
        }
    }

    override suspend fun getMoviesByCategory(categorySlug: String, page: Int, limit: Int): List<Movie> {
        val response = phimApiService.getMoviesByCategory(categorySlug, page, limit)
        return response.data.items.map { movieDto ->
            Movie(
                id = movieDto.id,
                title = movieDto.name,
                originTitle = movieDto.originName,
                slug = movieDto.slug,
                posterUrl = response.data.appDomainCdnImage + '/' + movieDto.posterUrl,
                thumbUrl = response.data.appDomainCdnImage + '/' + movieDto.thumbUrl,
                year = movieDto.year,
                modifiedTime = "",
                imdbId = null,
                type = null
            )
        }
    }

    override suspend fun getSeriesMovies(page: Int, limit: Int): List<Movie> {
        val response = phimApiService.getSeriesMovies(page, limit)
        return response.data.items.map { movieDto ->
            Movie(
                id = movieDto.id,
                title = movieDto.name,
                originTitle = movieDto.originName,
                slug = movieDto.slug,
                posterUrl = response.data.appDomainCdnImage + '/' + movieDto.posterUrl,
                thumbUrl = response.data.appDomainCdnImage + '/' + movieDto.thumbUrl,
                year = movieDto.year,
                modifiedTime = "",
                imdbId = null,
                type = "series"
            )
        }
    }

    override suspend fun getSingleMovies(page: Int, limit: Int): List<Movie> {
        val response = phimApiService.getSingleMovies(page, limit)
        return response.data.items.map { movieDto ->
            Movie(
                id = movieDto.id,
                title = movieDto.name,
                originTitle = movieDto.originName,
                slug = movieDto.slug,
                posterUrl = response.data.appDomainCdnImage + '/' + movieDto.posterUrl,
                thumbUrl = response.data.appDomainCdnImage + '/' + movieDto.thumbUrl,
                year = movieDto.year,
                modifiedTime = "",
                imdbId = null,
                type = "single"
            )
        }
    }

    override suspend fun getAnimeMovies(page: Int, limit: Int): List<Movie> {
        val response = phimApiService.getAnimeMovies(page, limit)
        return response.data.items.map { movieDto ->
            Movie(
                id = movieDto.id,
                title = movieDto.name,
                originTitle = movieDto.originName,
                slug = movieDto.slug,
                posterUrl = response.data.appDomainCdnImage + '/' + movieDto.posterUrl,
                thumbUrl = response.data.appDomainCdnImage + '/' + movieDto.thumbUrl,
                year = movieDto.year,
                modifiedTime = "",
                imdbId = null,
                type = "anime"
            )
        }
    }

    override suspend fun getMovieDetail(slug: String): MovieDetailResponseDto {
        return phimApiService.getMovieDetail(slug)
    }

    override suspend fun searchMovies(
        keyword: String?,
        page: Int,
        limit: Int,
        typeList: String?,
        sortField: String?,
        sortType: String?,
        sortLang: String?,
        category: String?,
        country: String?,
        year: Int?
    ): SearchMoviesResponseDto {
        return phimApiService.searchMovies(
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
