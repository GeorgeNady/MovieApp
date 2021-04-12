package com.george.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.george.movieapp.Adapters.TopRatedAdapter
import com.george.movieapp.R
import com.george.movieapp.databinding.FragmentTopRatedMoviesBinding
import com.george.movieapp.ui.MoviesActivity
import com.george.movieapp.ui.MoviesViewModel
import com.george.movieapp.utiles.Constants
import com.george.movieapp.utiles.DepthPageTransformer
import com.george.movieapp.utiles.Resource
import com.google.android.material.snackbar.Snackbar


class TopRatedMoviesFragment : Fragment(R.layout.fragment_top_rated_movies) {

    private var _binding: FragmentTopRatedMoviesBinding? = null
    private val binding get() = _binding!!

    companion object const private val TAG = "TopRatedMoviesFragment"
    lateinit var viewModel: MoviesViewModel
    private lateinit var topRatedAdapter: TopRatedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopRatedMoviesBinding.inflate(layoutInflater, container, false)
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
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.to_movieDetails_destination,
                bundle
            )
        }

    }

    @SuppressLint("ShowToast")
    private fun topRatedMoviesObserve() {
        viewModel.topRatedMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { topRatedResponse ->
                        if (topRatedResponse.results.size < 1) binding.ivNotFound.visibility = View.VISIBLE
                        topRatedAdapter.differ.submitList(topRatedResponse.results.toList())
                        val totalPages =
                            topRatedResponse.total_results / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.topRatedPage == totalPages
                        if (isLastPage) {
                            Snackbar.make(requireView().rootView,"End of Result",Snackbar.LENGTH_LONG)
                                .setAnchorView(binding.v)
                                .show()
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(requireView().rootView,"An Error occurred: $message",Snackbar.LENGTH_LONG)
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
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLastPage = true
    }

    private var isLoading = false
    private var isLastPage = false
    var isScrolling = false

    var lastItemIndex = 19

    private val changeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (position == lastItemIndex) {
                Log.d(TAG, position.toString())
                viewModel.getTopRatedMovies()
                lastItemIndex += 20
                Log.d(TAG, lastItemIndex.toString())
            } else Log.d(TAG, "$position not last page")
            super.onPageSelected(position)
        }
    }

    private fun setupRecyclerView() {
        topRatedAdapter = TopRatedAdapter()
        binding.topRatedViewPager.apply {
            adapter = topRatedAdapter
            setPageTransformer(DepthPageTransformer())
            registerOnPageChangeCallback(changeListener)
            // orientation = ViewPager2.ORIENTATION_VERTICAL
            beginFakeDrag()
            fakeDragBy(-2f)
            endFakeDrag()

        }
    }
}