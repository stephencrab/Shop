package com.stephen.shop.view

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.stephen.shop.model.Item

class FirestoreQueryLiveData : LiveData<List<Item>>(), EventListener<QuerySnapshot> {

    lateinit var registration: ListenerRegistration
    var isRegistered = false
    var query = FirebaseFirestore.getInstance()
        .collection("items")
        .orderBy("viewCount", Query.Direction.DESCENDING)
        .limit(10)

    override fun onActive() {
        registration = query.addSnapshotListener(this)
        isRegistered = true
    }

    override fun onInactive() {
        if (isRegistered) {
            registration.remove()
            isRegistered = false
        }
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
        if (querySnapshot !=null && !querySnapshot.isEmpty) {
            var list = mutableListOf<Item>()
            for (doc in querySnapshot.documents) {
                val item = doc.toObject(Item::class.java)?: Item()
                item.id = doc.id
                list.add(item)
            }
            value = list
        }
    }

    fun setCategory(categoryId: String) {
        if (isRegistered) {
            registration.remove()
            isRegistered = false
        }
        if (categoryId.length > 0) {
            query = FirebaseFirestore.getInstance()
                .collection("items")
                .whereEqualTo("category", categoryId)
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        } else {
            query = FirebaseFirestore.getInstance()
                .collection("items")
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        }
        registration = query.addSnapshotListener(this)
        isRegistered = true
    }
}