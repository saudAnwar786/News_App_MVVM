package com.example.myapplication.repositories

import com.example.myapplication.api.NewsApi
import com.example.myapplication.db.NewsDb
import com.example.myapplication.models.Article
import retrofit2.Retrofit
import javax.inject.Inject

class NewsRepository @Inject constructor (
       private val retrofit: Retrofit,
       private val newsDb: NewsDb
) {

       suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
              retrofit.create(NewsApi::class.java).getBreakingNews(countryCode, pageNumber)

       suspend fun getSearchNews(searchQuery:String, pageNumber: Int) =
              retrofit.create(NewsApi::class.java).getSearchNews(searchQuery,pageNumber)


       suspend fun addArticle(article: Article) = newsDb.dao.addArticle(article)
       suspend fun deleteArticle(article: Article) = newsDb.dao.deleteArticle((article))
       fun getAllSavedArticles() = newsDb.dao.getSavedArticles()
}