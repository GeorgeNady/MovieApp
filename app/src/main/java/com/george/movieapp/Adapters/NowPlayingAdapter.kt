package com.george.movieapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.george.movieapp.R
import com.george.movieapp.models.now_playing.*
import com.george.movieapp.databinding.ItemMovieRvBinding
import com.george.movieapp.utiles.Constants.BASE_IMAGE_URL

class NowPlayingAdapter:RecyclerView.Adapter<NowPlayingAdapter.NowPlatingViewHolder>() {

    inner class NowPlatingViewHolder (val binding : ItemMovieRvBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Result>() {

        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * this is a tool it take our **two lists** and **compares** them and calculate the **differences**
     * */
    val differ = AsyncListDiffer(this, differCallBack)

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NowPlatingViewHolder(
            ItemMovieRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NowPlatingViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.binding.apply {
            Glide.with(root)
                .load(BASE_IMAGE_URL + currentItem.poster_path)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(R.drawable.ic_image_search)
                .fallback(R.drawable.ic_image_search)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivMoviePoster)
            tvMovieTitle.text = currentItem.title
            tvMoveOverview.text = currentItem.overview
            tvMoveRate.text = currentItem.vote_average.toString()

            root.setOnClickListener {
                onItemClickListener?.let {
                    it(currentItem)
                }
            }
        }
    }


    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }
}