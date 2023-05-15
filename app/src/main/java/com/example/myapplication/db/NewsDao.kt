package com.example.myapplication.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.models.Article

@Dao
interface NewsDao {

    @Upsert
    suspend fun addArticle(article:Article):Long

    @Delete
    suspend fun deleteArticle(article: Article)


    @Query("SELECT * FROM article ")
    fun getSavedArticles():LiveData<List<Article>>



}