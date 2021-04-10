package com.george.movieapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.george.movieapp.repositories.MoviesRepository

@Suppress("UNCHECKED_CAST")
class MoviesViewModelProviderFactory(
    val app: Application,
    val repo: MoviesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MoviesViewModel(app, repo) as T
    }

}