package com.george.movieapp.models.now_playing

data class NowPlayingResponse(
    val dates: Dates,
    val page: Int,
    val results: MutableList<Result>,
    val total_pages: Int,
    val total_results: Int
)