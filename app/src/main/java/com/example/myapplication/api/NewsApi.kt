package com.example.myapplication.api

import com.example.myapplication.util.Constants
import com.example.myapplication.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "ind",
        @Query("page")
        pageNumber:Int =1,
        @Query("apiKey")
        apiKey:String = Constants.API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getSearchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber:Int =1,
        @Query("apiKey")
        apiKey:String = Constants.API_KEY
    ):Response<NewsResponse>
}