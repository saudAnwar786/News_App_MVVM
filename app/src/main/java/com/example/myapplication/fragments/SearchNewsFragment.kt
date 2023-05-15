package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.NewsAdapter
import com.example.myapplication.databinding.FragmentSearchNewsBinding
import com.example.myapplication.models.Article
import com.example.myapplication.util.Constants
import com.example.myapplication.util.Resource
import com.example.myapplication.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment:Fragment(R.layout.fragment_search_news) {

    private val newsViewModel : NewsViewModel by viewModels()
    private lateinit var binding: FragmentSearchNewsBinding
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
        binding = FragmentSearchNewsBinding.bind(view)

        setUpRecyclerView()
        newsAdapter.setOnItemClickListener(object :NewsAdapter.OnItemClickListener{
            override fun onItemClick(article: Article) {
                val bundle = Bundle().apply {
                    putSerializable("article",article)
                }
                findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle)
            }
        })
        var job: Job? = null
        binding.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        newsViewModel.getSearchNews(editable.toString())
                    }
                }
            }

        }
        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response->
             when(response){
                 is Resource.Error -> {
                     hideProgressBar()
                     response.message?.let {
                         Log.e("Search Fragment Error","")
                     }
                 }
                 is Resource.Loading -> {
                     showProgressBar()
                 }
                 is Resource.Success -> {
                     hideProgressBar()
                     response.data?.let { newsResponse ->
                          newsAdapter.differ.submitList(newsResponse.articles.toList())

                         val totalPages = newsResponse.totalResults/ Constants.QUERY_PAGE_SIZE + 2
                         isLastPage = totalPages == newsViewModel.searchNewsPage
                     }

                 }

             }

        })

    }

    fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }
    fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    var isLoading = false
    var isLastPage =false
    var isScrolling = false
    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                newsViewModel.getBreakingNews("us")
                isScrolling = false
            }else{
                binding.rvSearchNews.setPadding(0,0,0,0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

    }
    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }
}