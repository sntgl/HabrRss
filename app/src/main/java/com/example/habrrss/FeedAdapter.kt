package com.example.habrrss

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habrrss.databinding.FeedItemBinding

class FeedAdapter : RecyclerView.Adapter<FeedViewHolder>() {

    private var items: List<FeedItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding =
            FeedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun newItems(list: List<FeedItem>) {
        items = list
        notifyDataSetChanged()
    }
}

