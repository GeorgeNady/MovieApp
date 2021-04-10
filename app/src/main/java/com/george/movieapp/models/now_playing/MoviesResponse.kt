package com.george.movieapp.models.now_playing

data class MoviesResponse(
    val dates: Dates,
    val page: Int,
    val results: MutableList<Result>,
    val total_pages: Int,
    val total_results: Int
)