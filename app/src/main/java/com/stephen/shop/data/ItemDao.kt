package com.stephen.shop.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stephen.shop.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item ORDER BY viewCount")
    fun getItems(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Item)
}