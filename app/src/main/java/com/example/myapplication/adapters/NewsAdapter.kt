package com.example.myapplication.adapters

import android.content.Context
import android.location.GnssAntennaInfo.Listener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AbsListView.RecyclerListener
import android.widget.AdapterView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemArticlePreviewBinding
import com.example.myapplication.models.Article

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    inner class NewsViewHolder(val binding: ItemArticlePreviewBinding):RecyclerView.ViewHolder(binding.root)


    private var onItemClickListener: OnItemClickListener? = null

    // Define the OnItemClickListener interface
    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }

    val differCallback = object: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return newItem.id == oldItem.id
        }
    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val listItem = differ.currentList[position]
        holder.binding.apply {
            tvSource.text = listItem.source.name
            tvDescription.text = listItem.description
            tvPublishedAt.text = listItem.publishedAt
            tvTitle.text = listItem.title
            val context  = root.context
            Glide.with(context).load(listItem.urlToImage)
                .centerCrop()
                .into(ivArticleImage)

            root.setOnClickListener {
                onItemClickListener?.onItemClick(listItem)
            }

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}