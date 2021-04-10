package com.george.movieapp.Adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.george.movieapp.R
import com.george.movieapp.databinding.ItemGeneresBinding
import com.george.movieapp.models.now_playing.*
import com.george.movieapp.databinding.ItemMovieViewPagerBinding
import com.george.movieapp.utiles.Constants
import com.george.movieapp.utiles.Constants.BASE_IMAGE_URL

class GeneresAdapter(
    val res:Resources
):RecyclerView.Adapter<GeneresAdapter.NowPlatingViewHolder>() {

    inner class NowPlatingViewHolder (val binding : ItemGeneresBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Int>() {

        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
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
            ItemGeneresBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NowPlatingViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.binding.apply {
            for (i in Constants.genres.indices) {
                if (currentItem == Constants.genres[i].id) {
                    tvGenres.text = Constants.genres[i].name
                }
            }
            if (position % 2 == 0) genresContainer.setBackgroundColor(res.getColor(R.color.magenta))
            else genresContainer.setBackgroundColor(res.getColor(R.color.lime_green))
        }
    }


}