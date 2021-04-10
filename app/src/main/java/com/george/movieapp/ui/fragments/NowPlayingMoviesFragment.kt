package com.george.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.movieapp.Adapters.NowPlayingAdapter
import com.george.movieapp.R
import com.george.movieapp.databinding.FragmentNowPlayingMoviesBinding
import com.george.movieapp.ui.MoviesActivity
import com.george.movieapp.ui.MoviesViewModel
import com.george.movieapp.utiles.Constants.QUERY_PAGE_SIZE
import com.george.movieapp.utiles.Resource
import com.google.android.material.snackbar.Snackbar

class NowPlayingMoviesFragment : Fragment(R.layout.fragment_now_playing_movies) {

    private var _binding : FragmentNowPlayingMoviesBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: MoviesViewModel
    private lateinit var nowPlayingAdapter : NowPlayingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingMoviesBinding.inflate(layoutInflater,container,false)
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

        nowPlayingMoviesObserve()

        nowPlayingAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movie",it)
            }
            findNavController().navigate(
                R.id.to_movieDetails_destination,
                bundle
            )
        }

        binding.srlNowPlating.setOnRefreshListener {
            viewModel.nowPlayingPage = 1
            viewModel.getNowPlayingMovies("ar")
            binding.srlNowPlating.isRefreshing = false
        }

    }

    @SuppressLint("ShowToast")
    private fun nowPlayingMoviesObserve() {
        viewModel.moviesMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { nowPlayingResponse ->
                        if (nowPlayingResponse.results.size < 1) binding.ivNotFound.visibility = View.VISIBLE
                        nowPlayingAdapter.differ.submitList(nowPlayingResponse.results.toList())
                        val totalPages = nowPlayingResponse.total_results / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.nowPlayingPage == totalPages
                        if (isLastPage) {
                            Snackbar.make(binding.root,"End of Result",Snackbar.LENGTH_LONG)
                                .setAnchorView(binding.v)
                                .show()
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(binding.root,"An Error occurred: $message",Snackbar.LENGTH_LONG)
                            .setAnchorView(binding.v)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
        binding.srlNowPlating.isRefreshing = false
    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLastPage = true
        binding.srlNowPlating.isRefreshing = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    /**
     * # RecyclerView [scrollListener] [scrollListener] for Pagination
     * */
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtTheBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtTheBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                viewModel.getNowPlayingMovies("ar")
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        nowPlayingAdapter = NowPlayingAdapter()
        binding.rvNowPlaying.apply {
            adapter = nowPlayingAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(this@NowPlayingMoviesFragment.scrollListener)
        }
    }

}