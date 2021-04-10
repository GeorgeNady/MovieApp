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
        getNowPlayingMovies("ar")
        getTopRatedMovies("ar")
    }

    /**
     * # Now Playing Movies
     * */
    fun getNowPlayingMovies(countryCode: String) = viewModelScope.launch {
        safeNowPlayingMovies(countryCode)
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

    private suspend fun safeNowPlayingMovies(countryCode: String) {
        moviesMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repo.getNowPlayingMovies(countryCode, nowPlayingPage)
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
    fun getTopRatedMovies(countryCode: String) = viewModelScope.launch {
        safeTopRatedMovies(countryCode)
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

    private suspend fun safeTopRatedMovies(countryCode: String) {
        topRatedMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repo.getTopRatedMovies(countryCode, topRatedPage)
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

}