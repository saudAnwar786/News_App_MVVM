package com.example.myapplication.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.util.Resource
import com.example.myapplication.models.Article
import com.example.myapplication.models.NewsResponse
import com.example.myapplication.repositories.NewsRepository
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    @ApplicationContext private val context: Context
) :ViewModel(){


    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

   val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
   var searchNewsPage =1
   var searchNewsResponse :NewsResponse? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null


    init {
        getBreakingNews("us")
    }
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)

    }
    fun getSearchNews(query:String) = viewModelScope.launch {
        safeSearchNewsCall(query)
    }

    private fun handleBreakingNews(response:Response<NewsResponse>): Resource<NewsResponse> {

         if(response.isSuccessful){
             response.body()?.let { responseResult->

                 breakingNewsPage++
                 if(breakingNewsResponse == null){
                     breakingNewsResponse = responseResult
                 }else{
                     val oldArticles = breakingNewsResponse?.articles
                     val newArticles = responseResult.articles
                     oldArticles?.addAll(newArticles)
                 }
                 return Resource.Success(breakingNewsResponse?:responseResult)
             }

         }
             return Resource.Error(response.message())

    }
    private fun handleSearchNews(response: Response<NewsResponse>): Resource<NewsResponse> {

        if(response.isSuccessful){
            response.body()?.let {responseResult->

                if(searchNewsResponse == null || oldSearchQuery!=newSearchQuery){
                    searchNewsPage = 1
                    searchNewsResponse = responseResult
                    oldSearchQuery = newSearchQuery
                }else{
                    searchNewsPage++
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = responseResult.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse?:responseResult)
            }
        }
        return Resource.Error(response.message())
    }

    suspend fun safeBreakingNewsCall(countryCode: String){

        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue( handleBreakingNews(response) )

            }else{
                breakingNews.postValue(Resource.Error("No internet Connection"))
            }

        }catch (t: Throwable){
            when(t){
               is IOException-> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Problem"))
            }
        }

    }
    suspend fun safeSearchNewsCall(query: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = repository.getSearchNews(query, searchNewsPage)
                searchNews.postValue(handleSearchNews(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException-> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Problem"))
            }
        }

    }
    fun addArticle(article: Article) = viewModelScope.launch {
        repository.addArticle(article)
    }
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }
    fun getAllSavedArticles() = repository.getAllSavedArticles()
    
    private fun hasInternetConnection(): Boolean {

        val connectivityManager = getApplication(context) .getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}