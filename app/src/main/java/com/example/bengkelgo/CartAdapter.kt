package com.example.bengkelgo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bengkelgo.CartItem
import com.example.bengkelgo.R

class CartAdapter(
    private val context: Context,
    private var cartItems: MutableList<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit, // partId, newQuantity
    private val onItemRemoved: (CartItem) -> Unit // partId
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "Rp %,.0f".format(item.price)
        holder.tvQuantity.text = item.quantity.toString()

        // TODO: Load image using Glide/Picasso and item.imageName
        val imageResId = context.resources.getIdentifier(item.imageName.substringBefore("."), "drawable", context.packageName)
        if (imageResId != 0) {
            holder.ivImage.setImageResource(imageResId)
        } else {
            holder.ivImage.setImageResource(R.drawable.placeholder_part_small)
        }


        holder.btnIncrease.setOnClickListener {
            val newQuantity = item.quantity + 1
            // Consider checking against stock here or in the activity
            onQuantityChanged(item, newQuantity)
        }

        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                val newQuantity = item.quantity - 1
                onQuantityChanged(item, newQuantity)
            } else {
                // If quantity becomes 0, treat as removal
                onItemRemoved(item)
            }
        }

        holder.btnRemove.setOnClickListener {
            onItemRemoved(item)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivCartItemImage)
        val tvName: TextView = itemView.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCartItemQuantity)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncreaseQuantity)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecreaseQuantity)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFromCart)
    }
}