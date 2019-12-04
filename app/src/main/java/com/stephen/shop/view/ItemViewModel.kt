package com.stephen.shop.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stephen.shop.model.Item

class ItemViewModel : ViewModel() {
    private var items = MutableLiveData<List<Item>>()
    private var firestoreQueryLiveData = FirestoreQueryLiveData()

    fun getItems() : FirestoreQueryLiveData {
        return firestoreQueryLiveData
    }

    fun setCategory(categoryId: String) {
        firestoreQueryLiveData.setCategory(categoryId)
    }
}