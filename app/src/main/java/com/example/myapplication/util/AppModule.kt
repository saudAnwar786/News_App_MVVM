package com.example.myapplication.util

import android.content.Context
import androidx.room.Room
import com.example.myapplication.db.NewsDao
import com.example.myapplication.db.NewsDb
import com.example.myapplication.repositories.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient):Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context:Context):NewsDb{
        return Room.databaseBuilder(context,NewsDb::class.java,"news_db")
            .build()

    }

    @Provides
    @Singleton
    fun provideRoomDao(newsDb: NewsDb) :NewsDao{
        return newsDb.dao
    }


}

