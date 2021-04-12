package com.george.movieapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.movieapp.Adapters.NowPlayingAdapter
import com.george.movieapp.R
import com.george.movieapp.databinding.FragmentSearchInMoviesBinding
import com.george.movieapp.models.now_playing.MoviesResponse
import com.george.movieapp.ui.MoviesActivity
import com.george.movieapp.ui.MoviesViewModel
import com.george.movieapp.utiles.Constants
import com.george.movieapp.utiles.Resource
import com.google.android.material.snackbar.Snackbar


class SearchInMoviesFragment : Fragment(R.layout.fragment_search_in_movies) {

    private var _binding : FragmentSearchInMoviesBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: MoviesViewModel
    private lateinit var searchAdapter : NowPlayingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchInMoviesBinding.inflate(layoutInflater,container,false)
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

        searchForMoviesObserve()

        searchAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movie",it)
            }
            findNavController().navigate(
                R.id.to_movieDetails_destination,
                bundle
            )
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text
            binding.ivNotFound.visibility = View.GONE
            viewModel.searchPage = 1
            query?.let {
                viewModel.searchForNews(it.toString())
            }
        }

        binding.srlSearch.setOnRefreshListener {
            viewModel.nowPlayingPage = 1
            viewModel.getNowPlayingMovies()
            binding.srlSearch.isRefreshing = false
        }

    }

    private fun searchForMoviesObserve() {
        viewModel.searchMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { searchResponse ->
                        if (searchResponse.results.size < 1) binding.ivNotFound.visibility = View.VISIBLE
                        searchAdapter.differ.submitList(searchResponse.results.toList())
                        val totalPages = searchResponse.total_results / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.nowPlayingPage == totalPages
                        if (isLastPage) {
                            Snackbar.make(requireView().rootView,"End of Result",Snackbar.LENGTH_LONG)
                                .setAnchorView(binding.v)
                                .show()                    }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(requireView().rootView,"An Error occurred: $message",Snackbar.LENGTH_LONG)
                            .setAnchorView(binding.v)
                            .show()                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.apply {
            progressBar.visibility = View.INVISIBLE
            searchProgressBar.visibility = View.INVISIBLE
            btnSearch.visibility = View.VISIBLE
        }
        isLoading = false

    }
    private fun showProgressBar() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            searchProgressBar.visibility = View.VISIBLE
            btnSearch.visibility = View.GONE
        }
        isLastPage = true }

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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtTheBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                viewModel.getNowPlayingMovies()
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
        searchAdapter = NowPlayingAdapter()
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(this@SearchInMoviesFragment.scrollListener)
        }
    }

}