package com.stephen.shop.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stephen.shop.data.ItemRepository
import com.stephen.shop.model.Item

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var itemRepository: ItemRepository

    init {
        itemRepository = ItemRepository(application)
    }

    fun getItems() : LiveData<List<Item>> {
        return itemRepository.getAllItems()
    }

    fun setCategory(categoryId: String) {
        itemRepository.setCategory(categoryId)
    }
}