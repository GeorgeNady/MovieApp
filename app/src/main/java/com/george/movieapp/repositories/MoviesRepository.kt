package com.george.movieapp.repositories

import com.george.movieapp.api.RetrofitInstance


/**
 * # purpose:
 * ### get the data form our Database and our remote data source from retrofit from our api
 *  */
class MoviesRepository(
    /*val db: MovieDatabase*/
) {

    /**
     * # Network
     * */
    suspend fun getNowPlayingMovies(
        language: String? = "ar",
        page: Int = 1,
    ) = RetrofitInstance.api.getNowPlayingMovies(countryCode = language, page = page)

    suspend fun getTopRatedMovies(
        language: String? = "ar",
        page: Int = 1,
    ) = RetrofitInstance.api.getTopRatedMovies(countryCode = language, page = page)

    suspend fun searchForMovies(
        language: String? = "ar",
        query: String,
        page: Int = 1,
        includeAdult: Boolean = true,
    ) = RetrofitInstance.api.searchForMovies(
        countryCode = language,
        query = query,
        page = page,
        includeAdult = includeAdult,
    )

    /**
     * # Database
     * */


}