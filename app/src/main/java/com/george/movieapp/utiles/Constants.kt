package com.george.movieapp.utiles

import com.george.movieapp.models.now_playing.Genre

object Constants {

    const val BASE_URL = "https://api.themoviedb.org"
    const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
    const val API_KEY = "b7d14eb3e5d989dd3d33b2e729639765"
    const val QUERY_PAGE_SIZE = 20
    const val QUERY_LANGUAGE = "en"
    const val QUERY_REGION = "EG"

    val genres = listOf(
        Genre(28,"Action"),
        Genre(12,"Adventure"),
        Genre(16,"Animation"),
        Genre(35,"Comedy"),
        Genre(80,"Crime"),
        Genre(99,"Documentary"),
        Genre(18,"Drama"),
        Genre(10751,"Family"),
        Genre(14,"Fantasy"),
        Genre(36,"History"),
        Genre(27,"Horror"),
        Genre(10402,"Music"),
        Genre(9648,"Mystery"),
        Genre(10749,"Romance"),
        Genre(878,"Science Fiction"),
        Genre(10770,"TV Movie"),
        Genre(53,"Thriller"),
        Genre(10752,"War"),
        Genre(37,"Western")
    )

}