package com.george.movieapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.george.movieapp.R
import com.george.movieapp.databinding.ActivityMoviesBinding
import com.george.movieapp.dp.MovieDatabase
import com.george.movieapp.repositories.MoviesRepository
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MoviesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMoviesBinding
    lateinit var viewModel: MoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.MovieApp)
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
    }

    private fun setListener() {
        blurBackground(binding.blurViewBottom, 20f)

        GlobalScope.launch (Dispatchers.Main){
            onStartViews()
        }

        // view model declaration
        val repo = MoviesRepository(MovieDatabase(applicationContext))
        val viewModelProviderFactory = MoviesViewModelProviderFactory(application,repo)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)
            .get(MoviesViewModel::class.java)

        // setup chip navigation bar
        binding.apply {
            chipNavBar.setItemSelected(R.id.fragment_now_playing,true)
            tvPageTitle.text = resources.getString(R.string.now_playing)
            chipNavBar.setOnItemSelectedListener( object : ChipNavigationBar.OnItemSelectedListener {
                override fun onItemSelected(id: Int) {
                    when (id) {
                        R.id.fragment_now_playing -> {
                            navHostFragment.findNavController().navigate(R.id.nowPlayingMoviesFragment)
                            tvPageTitle.text = resources.getString(R.string.now_playing)
                        }
                        R.id.fragment_top_rated -> {
                            navHostFragment.findNavController().navigate(R.id.topRatedMoviesFragment)
                            tvPageTitle.text = resources.getString(R.string.top_rated)
                        }
                        R.id.fragment_search_in_movie -> {
                            navHostFragment.findNavController().navigate(R.id.searchInMoviesFragment)
                            tvPageTitle.text = resources.getString(R.string.search)
                        }
                        R.id.fragment_favorite -> {
                            navHostFragment.findNavController().navigate(R.id.favoriteFragment)
                            tvPageTitle.text = resources.getString(R.string.favorite)
                        }
                    }
                }
            })
        }

    }

    /**
     * ### *setup blur background for chip navigation view*
     * */
    private fun blurBackground(blurView: BlurView, radius: Float) {
        val decorView = window.decorView
        val rootView = binding.mainContainer
        val windowBackground = decorView.background
        blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(applicationContext))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

    }

    private suspend fun onStartViews() {
        GlobalScope.launch (Dispatchers.Main) {
            binding.apply {
                tobAppBar.animate().scaleY(1f).alpha(1f)
                    .setDuration(300).start()
                blurViewBottom.animate().scaleY(1f).alpha(1f)
                    .setDuration(300).start()
                delay(300)
                tvAppName.animate().translationY(0f).alpha(1f)
                    .setDuration(300).start()
                tvAppIcon.animate().translationY(0f).alpha(1f)
                    .setDuration(300).start()
                delay(300)
                tvPageTitle.animate().translationX(0f).alpha(1f)
                    .setDuration(300).start()
                delay(300)
                navHostFragment.animate().alpha(1f).setDuration(300).start()
            }
        }
    }
    
}