package com.george.movieapp.utiles

/**
 * that is a class recommended by Google to be used to wrap around our network responses
 * and that will be a generic class
 * and its very useful to differentiate between successful and error responses
 * and also help us to handle the loading state =>
 * so when we make a response we can show a progress bar while that response is processing
 * and when we get the answer then we can use this class to tell us whether that answer was successful or an error
 * and depending on that we can handle that error or show that successful response
 *
 *
 * ## sealed : it is kind of abstract class
 * but we can define which classes are allowed to inherit from that Resource class
 * */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    /**
     * in here we will define three different classes
     *
     * and only those are allowed to inherit from Resource class
     */
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String,data: T? = null) : Resource<T>(data , message)
    class Loading<T> : Resource<T>()

}
