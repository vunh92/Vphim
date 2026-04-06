package com.vunh.android.vphim.data.mapper

import com.vunh.android.vphim.data.remote.dto.CategoryDto
import com.vunh.android.vphim.domain.model.Category

fun CategoryDto.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        slug = slug
    )
}
