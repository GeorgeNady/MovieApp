package com.george.movieapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.george.movieapp.MoviesApplication
import com.george.movieapp.models.now_playing.MoviesResponse
import com.george.movieapp.models.now_playing.Result
import com.george.movieapp.repositories.MoviesRepository
import com.george.movieapp.utiles.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MoviesViewModel(
    app: Application,
    val repo: MoviesRepository
) : AndroidViewModel(app) {

    var moviesMovies : MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    var nowPlayingPage = 1
    var moviesResponse : MoviesResponse? = null

    val topRatedMovies : MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    var topRatedPage = 1
    var topRatedResponse : MoviesResponse? = null

    init {
        getNowPlayingMovies()
        getTopRatedMovies()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// NETWORK HANDLING SECTION ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * # Now Playing Movies
     * */
    fun getNowPlayingMovies() = viewModelScope.launch {
        safeNowPlayingMovies()
    }

    private fun handleNowPlayingMoviesResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                nowPlayingPage++
                if (moviesResponse == null) {
                    moviesResponse = resultResponse
                } else {
                    val oldMovies = moviesResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(moviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeNowPlayingMovies() {
        moviesMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repo.getNowPlayingMovies(nowPlayingPage)
                moviesMovies.postValue(handleNowPlayingMoviesResponse(response))
            } else {
                moviesMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> moviesMovies.postValue(Resource.Error("Network Failure"))
                else -> moviesMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    /**
     * # Top Rated Movies
     * */
    fun getTopRatedMovies() = viewModelScope.launch {
        safeTopRatedMovies()
    }

    private fun handleTopRatedResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topRatedPage++
                if (topRatedResponse == null) {
                    topRatedResponse = resultResponse
                } else {
                    val oldMovies = topRatedResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(topRatedResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeTopRatedMovies() {
        topRatedMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repo.getTopRatedMovies(topRatedPage)
                topRatedMovies.postValue(handleTopRatedResponse(response))
            } else {
                topRatedMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topRatedMovies.postValue(Resource.Error("Network Failure"))
                else -> topRatedMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    /**
     * # Search For Movies
     * */
    val searchMovies : MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    var searchPage = 1
    var searchResponse : MoviesResponse? = null
    var isAdult = true

    fun searchForNews(query: String) = viewModelScope.launch {
        safeSearchForMovies(query)
    }

    private fun handleSearchForMoviesResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchPage++
                if (searchResponse == null) {
                    searchResponse = resultResponse
                } else {
                    val oldMovies = searchResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(searchResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchForMovies(query: String) {
        searchMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repo.searchForMovies(query,searchPage,isAdult)
                searchMovies.postValue(handleSearchForMoviesResponse(response))
            } else {
                searchMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMovies.postValue(Resource.Error("Network Failure"))
                else -> searchMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    /**
     * # Checking Internet Connection
     * */
    private fun hasInternetConnection(): Boolean {
        // we need to call the connectivity manager
        val connectivityManager = getApplication<MoviesApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// DATABASE HANDLING SECTION ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun saveMovie(movie:Result) = viewModelScope.launch {
        repo.saveMovie(movie)
    }

    fun deleteMovie(movie: Result) = viewModelScope.launch {
        repo.delete(movie)
    }

    fun getFavoriteMovies() = repo.getFavoriteMovies()


}