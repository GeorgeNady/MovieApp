package com.george.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.george.movieapp.Adapters.GeneresAdapter
import com.george.movieapp.Adapters.NowPlayingAdapter
import com.george.movieapp.R
import com.george.movieapp.databinding.FragmentMovieDetailsBinding
import com.george.movieapp.ui.MoviesActivity
import com.george.movieapp.ui.MoviesViewModel
import com.george.movieapp.utiles.Constants
import com.george.movieapp.utiles.Constants.BASE_IMAGE_URL
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private val args: MovieDetailsFragmentArgs by navArgs()
    private var _binding : FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private companion object const val TAG = "MovieDetailsFragment"
    private lateinit var generesAdapter : GeneresAdapter
    private lateinit var viewModel: MoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setListener()
    }

    @SuppressLint("SetTextI18n", "ShowToast")
    private fun setListener() {
        // logic here
        viewModel = (activity as MoviesActivity).viewModel

        val movie = args.movie
        Glide.with(requireContext())
            .load(BASE_IMAGE_URL + movie.poster_path)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivMoviePoster)
        binding.apply {
            tvMovieTitle.text = movie.title
            tvDescription.text = movie.overview
            tvVoteAvg.text = movie.vote_average.toString()
            tvVoteCount.text = "${movie.vote_count} Votes"
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveMovie(movie)
            Log.d(TAG, movie.toString())
            Snackbar.make(binding.root,"Movie Saved in the Favorite List", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.view)
                .show()
        }
    }

    private fun setupRecyclerView() {
        generesAdapter = GeneresAdapter(resources)
        val generesList = mutableListOf<Int>()
        for (i in args.movie.genre_ids) {
            for (x in Constants.genres) {
                if (i == x.id) {
                    generesList.add(i)
                }
            }
        }
        generesAdapter.differ.submitList(generesList)
        binding.rvGeneres.apply {
            adapter = generesAdapter
        }
    }

}