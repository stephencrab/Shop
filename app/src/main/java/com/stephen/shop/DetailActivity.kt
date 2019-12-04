package com.stephen.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.stephen.shop.model.Item
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private val TAG = DetailActivity::class.java.simpleName
    lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        item = intent.getParcelableExtra<Item>("ITEM")
        Log.d(TAG, "onCreate:  ${item.id} / ${item.title}")
        web.settings.javaScriptEnabled = true
        web.loadUrl(item.content)
    }

    override fun onStart() {
        super.onStart()
        item.viewCount++
        item.id?.let {
            FirebaseFirestore.getInstance().collection("items")
                .document(it).update("viewCount",item.viewCount)
        }
    }
}
