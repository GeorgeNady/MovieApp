package com.george.movieapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.movieapp.Adapters.NowPlayingAdapter
import com.george.movieapp.R
import com.george.movieapp.databinding.FragmentFavoriteBinding
import com.george.movieapp.databinding.FragmentMovieDetailsBinding
import com.george.movieapp.ui.MoviesActivity
import com.george.movieapp.ui.MoviesViewModel
import com.google.android.material.snackbar.Snackbar


class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding : FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    companion object const private val TAG = "FavoriteFragment"
    lateinit var viewModel: MoviesViewModel
    private lateinit var favoriteAdapter: NowPlayingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater,container,false)
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

    private fun setListener() {
        // logic here
        viewModel = (activity as MoviesActivity).viewModel

        viewModel.getFavoriteMovies().observe(viewLifecycleOwner, Observer { movies ->
            if (movies.isEmpty()) binding.ivNotFound.visibility = View.VISIBLE
            favoriteAdapter.differ.submitList(movies)
        })

        favoriteAdapter.setOnItemClickListener {
            Log.d(TAG,it.toString())
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.to_movieDetails_destination,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val movie = favoriteAdapter.differ.currentList[position]
                viewModel.deleteMovie(movie)
                Snackbar.make(requireView().rootView,"Successfully deleted article",Snackbar.LENGTH_LONG).apply {
                    anchorView = binding.v
                    setAction("Undo") {
                        viewModel.saveMovie(movie)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavorite)
        }

    }

    private fun setupRecyclerView() {
        favoriteAdapter = NowPlayingAdapter()
        binding.rvFavorite.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}