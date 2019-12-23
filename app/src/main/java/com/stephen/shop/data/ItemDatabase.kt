package com.stephen.shop.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stephen.shop.model.Item

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase: RoomDatabase() {
    abstract fun getItemDao(): ItemDao

    companion object {
        private lateinit var context: Context
        private val database: ItemDatabase by lazy {
            Room.databaseBuilder(context, ItemDatabase::class.java,"mydb" )
                .allowMainThreadQueries()
                .build()
        }
        fun getDatabase(context: Context): ItemDatabase {
            Companion.context = context
            return database
        }
    }
}