package com.george.movieapp.ui.fragments

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
import com.george.movieapp.Adapters.TopRatedAdapter
import com.george.movieapp.R
import com.george.movieapp.databinding.FragmentTopRatedMoviesBinding
import com.george.movieapp.ui.MoviesActivity
import com.george.movieapp.ui.MoviesViewModel
import com.george.movieapp.utiles.Constants
import com.george.movieapp.utiles.Resource


class TopRatedMoviesFragment : Fragment(R.layout.fragment_top_rated_movies) {

    private var _binding : FragmentTopRatedMoviesBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: MoviesViewModel
    private lateinit var topRatedAdapter : TopRatedAdapter
    private val mActivity = MoviesActivity()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopRatedMoviesBinding.inflate(layoutInflater,container,false)
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

        topRatedMoviesObserve()

        topRatedAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_topRatedMoviesFragment_to_movieDetailsFragment,
                bundle
            )
        }

    }

    private fun topRatedMoviesObserve() {
        viewModel.moviesMovies.observe(viewLifecycleOwner, Observer { response ->
           when(response) {
               is Resource.Success -> {
                   hideProgressBar()
                   response.data?.let { topRatedResponse ->
                       topRatedAdapter.differ.submitList(topRatedResponse.results.toList())
                       val totalPages = topRatedResponse.total_results / Constants.QUERY_PAGE_SIZE + 2
                       isLastPage = viewModel.topRatedPage == totalPages
                       if (isLastPage) {
                           TODO("logic here")
                       }
                   }
               }
               is Resource.Error -> {
                   hideProgressBar()
                   response.message?.let { message ->
                       Toast.makeText(activity,"An Error occurred: $message", Toast.LENGTH_LONG).show()
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
    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLastPage = true
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtTheBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                viewModel.getNowPlayingMovies("eg")
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
        topRatedAdapter = TopRatedAdapter()
        binding.topRatedViewPager.apply {
            adapter = topRatedAdapter
            /*addOnScrollListener(this@TopRatedMoviesFragment.scrollListener)*/
        }
    }
}