package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.NewsAdapter
import com.example.myapplication.databinding.FragmentSavedNewsBinding
import com.example.myapplication.models.Article
import com.example.myapplication.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment:Fragment(R.layout.fragment_saved_news) {

    private val newsViewModel: NewsViewModel by viewModels()
    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding  = FragmentSavedNewsBinding.bind(view)
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener(object : NewsAdapter.OnItemClickListener{
            override fun onItemClick(article: Article) {
                val bundle = Bundle().apply {
                    putSerializable("article",article)
                }
                findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment,bundle)
            }
        })

        newsViewModel.getAllSavedArticles().observe(viewLifecycleOwner, Observer{ articles->
            newsAdapter.differ.submitList(articles.toList())
        })

    }

    private fun setUpRecyclerView(){
        binding.rvSavedNews.apply {
            newsAdapter = NewsAdapter()
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}