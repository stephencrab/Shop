package com.stephen.shop.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import com.stephen.shop.model.Item
import com.stephen.shop.view.FirestoreQueryLiveData

class ItemRepository (application: Application) {
    private var itemDao: ItemDao
    private lateinit var items: LiveData<List<Item>>
    private var firestoreQueryLiveData = FirestoreQueryLiveData()
    private var newtwork = false

    init {
        itemDao = ItemDatabase.getDatabase(application).getItemDao()
        items = itemDao.getItems()
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        newtwork = networkInfo.isConnected
    }

    fun getAllItems() : LiveData<List<Item>> {
        if (newtwork) {
            items = firestoreQueryLiveData
        } else {
            items = itemDao.getItems()
        }
        return items
    }

    fun setCategory(categoryId: String) {
        if (newtwork) {
            firestoreQueryLiveData.setCategory(categoryId)
        } else {
            items = itemDao.getItemByCategory(categoryId)
        }
    }
}