package com.george.movieapp.api

import com.george.movieapp.utiles.Constants.BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    /**
     * # purpose
     * *this Retrofit Singleton class created for:*
     * ### enables us to make request from everywhere in our code
     * */
    companion object {

        /**
         * ### this lazy means that we just initialize this block of code once
         * */
        private val retrofit by lazy {

            /**
             * we will attach this to our retrofit object
             *
             * to be able to see *which request we are actually making and what response are*
             * ## *which request we are actually making and what response are*
             **/
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val clint = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clint)
                .build()
        }

        val api: MoviesAPI by lazy {
            retrofit.create(MoviesAPI::class.java)
        }

    }
}
