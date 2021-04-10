package com.george.movieapp.api

import com.george.movieapp.models.now_playing.MoviesResponse
import com.george.movieapp.utiles.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MoviesAPI {

    /**
     * # GET : movie/now_playing
     **
     * ## Query String :
     * * [api_key] [com.george.watchyourmovies.utiles.Constants.API_KEY] **required**
     * * language : [String] [String]
     * * page : [Integer] [Int]
     * * region : [String] [String]
     *
     * Get a list of movies in theatres.
     *
     * This is a release type query that looks for all movies that have a release type of 2 or 3 within the specified date range.
     */
    @Headers("Accept: application/json", "Content-Type:  application/json;charset=utf-8")
    @GET("/3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") countryCode: String? = "ar",
        @Query("page") page: Int = 1
    ) : Response<MoviesResponse>

    /**
     * # GET : movie/top_rated
     **
     * ## Query String :
     * * [api_key] [com.george.watchyourmovies.utiles.Constants.API_KEY] **required**
     * * language : [String] [String]
     * * page : [Integer] [Int]
     * * region : [String] [String]
     *
     * Get the top rated movies on TMDb.
     */
    @Headers("Accept: application/json", "Content-Type:  application/json;charset=utf-8")
    @GET("/3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") countryCode: String? = "ar",
        @Query("page") page: Int = 1
    ) : Response<MoviesResponse>

    /**
     * # GET : search/movie
     **
     * ## Query String :
     * * [api_key] [com.george.watchyourmovies.utiles.Constants.API_KEY] **required**
     * * language : [String] [String]
     * * query : [String] [String] **required**
     * * page : [Integer] [Int]
     * * include_adult : [Boolean] [Boolean]
     * * region : [String] [String]
     * * year : [Integer] [Integer]
     * * primary_release_year : [Integer] [Integer]
     *
     * Search for movies.
     */
    @Headers("Accept: application/json", "Content-Type:  application/json;charset=utf-8")
    @GET("/3/movie/top_rated")
    suspend fun searchForMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") countryCode: String? = "ar",
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = true
    ) /*: Response<???>*/

}