package com.vunh.android.vphim.data.mapper

import com.vunh.android.vphim.data.remote.dto.MovieDto
import com.vunh.android.vphim.domain.model.Movie

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = name,
        originTitle = originName,
        slug = slug,
        posterUrl = posterUrl,
        thumbUrl = thumbUrl,
        year = year,
        modifiedTime = modified.time,
        imdbId = imdb.id,
        type = tmdb.type,
    )
}
