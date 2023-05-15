package com.example.myapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.models.Article

@Database(entities = [Article::class]
, version = 1)
@TypeConverters(Converters::class)
abstract class NewsDb:RoomDatabase() {
    abstract val dao:NewsDao

}