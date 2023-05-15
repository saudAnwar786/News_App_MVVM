package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentArticleBinding
import com.example.myapplication.viewmodels.NewsViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment:Fragment(R.layout.fragment_article) {
    private val newsViewModel : NewsViewModel by viewModels()
    private lateinit var binding: FragmentArticleBinding
    val args:ArticleFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(args.article.url)
        }

        binding.fab.setOnClickListener {
            newsViewModel.addArticle(args.article)
            Snackbar.make(view,"Saved !!",LENGTH_SHORT).show()
        }
    }

}