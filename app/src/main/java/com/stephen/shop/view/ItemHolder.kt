package com.stephen.shop.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stephen.shop.R
import com.stephen.shop.model.Item
import kotlinx.android.synthetic.main.item_row.view.*

class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    var titleText = view.item_title
    var priceText = view.item_price
    var image = view.item_image
    var countText = view.item_count

    fun bindTo(item: Item) {
        titleText.setText(item.title)
        priceText.setText(item.price.toString())
        Glide.with(itemView.context)
            .load(item.imgUrl)
            .apply(RequestOptions().override(200))
            .into(image)
        countText.setText(item.viewCount.toString())
        countText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.eye, 0,0,0)
    }
}