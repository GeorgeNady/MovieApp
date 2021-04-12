package com.george.movieapp.repositories

import com.george.movieapp.api.RetrofitInstance
import com.george.movieapp.dp.MovieDatabase
import com.george.movieapp.models.now_playing.Result


/**
 * # purpose:
 * ### get the data form our Database and our remote data source from retrofit from our api
 *  */
class MoviesRepository(
    private val db: MovieDatabase
) {

    /**
     * # Network
     * */
    suspend fun getNowPlayingMovies(
        page: Int = 1
    ) = RetrofitInstance.api.getNowPlayingMovies(page = page)

    suspend fun getTopRatedMovies(
        page: Int = 1,
    ) = RetrofitInstance.api.getTopRatedMovies(page = page)

    suspend fun searchForMovies(
        query: String,
        page: Int = 1,
        includeAdult: Boolean = true,
    ) = RetrofitInstance.api.searchForMovies(
        query = query,
        page = page,
        includeAdult = includeAdult,
    )

    /**
     * # Database
     * */
    suspend fun saveMovie(movie:Result) = db.getMoviesDao().upsertMovie(movie)

    suspend fun delete(movie:Result) = db.getMoviesDao().deleteMovie(movie)

    fun getFavoriteMovies() = db.getMoviesDao().getAllMovies()

}