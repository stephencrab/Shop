package com.stephen.shop.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stephen.shop.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item ORDER BY viewCount")
    fun getItems(): LiveData<List<Item>>

    @Query("SELECT * FROM Item WHERE category == :categoryId ORDER BY viewCount")
    fun getItemByCategory(categoryId: String): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Item)
}