package com.vunh.android.vphim.di

import com.vunh.android.vphim.data.repository.MovieRepositoryImpl
import com.vunh.android.vphim.data.repository.UserRepositoryImpl
import com.vunh.android.vphim.domain.repository.MovieRepository
import com.vunh.android.vphim.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
