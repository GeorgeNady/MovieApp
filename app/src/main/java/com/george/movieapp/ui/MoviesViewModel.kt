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
import com.george.movieapp.models.now_playing.NowPlayingResponse
import com.george.movieapp.repositories.MoviesRepository
import com.george.movieapp.utiles.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MoviesViewModel(
    app: Application,
    val repo: MoviesRepository
) : AndroidViewModel(app) {

    /**
     * # Now Playing Movies
     * */
    var nowPlayingMovies : MutableLiveData<Resource<NowPlayingResponse>> = MutableLiveData()
    var nowPlayingPage = 1
    var nowPlayingResponse : NowPlayingResponse? = null

    init {
        getNowPlayingMovies("ar")
    }

    fun getNowPlayingMovies(countryCode: String) = viewModelScope.launch {
        safeNowPlayingMovies(countryCode)
    }

    private fun handleNowPlayingMoviesResponse(response: Response<NowPlayingResponse>): Resource<NowPlayingResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                nowPlayingPage++
                if (nowPlayingResponse == null) {
                    nowPlayingResponse = resultResponse
                } else {
                    val oldMovies = nowPlayingResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(nowPlayingResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeNowPlayingMovies(countryCode: String) {
        nowPlayingMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repo.getNowPlayingMovies(countryCode, nowPlayingPage)
                nowPlayingMovies.postValue(handleNowPlayingMoviesResponse(response))
            } else {
                nowPlayingMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> nowPlayingMovies.postValue(Resource.Error("Network Failure"))
                else -> nowPlayingMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    /**
     * # Top Rated Movies
     * */
    val topRatedMovies : MutableLiveData<Resource<NowPlayingResponse>> = MutableLiveData()
    var topRatedPage = 1
    var topRatedResponse : NowPlayingResponse? = null



    /**
     * # Search For Movies
     * */
    val searchMovies : MutableLiveData<Resource<NowPlayingResponse>> = MutableLiveData()
    var searchPage = 1
    var searchResponse : NowPlayingResponse? = null



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