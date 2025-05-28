package com.example.bengkelgo.adapter // Create 'adapter' package

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bengkelgo.R
import com.example.bengkelgo.SparePart
import com.example.bengkelgo.SparePartDetailActivity // Will create this next

class SparePartAdapter(
    private val context: Context,
    private var partList: List<SparePart>
) : RecyclerView.Adapter<SparePartAdapter.SparePartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparePartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_spare_part, parent, false)
        return SparePartViewHolder(view)
    }

    override fun onBindViewHolder(holder: SparePartViewHolder, position: Int) {
        val part = partList[position]
        holder.tvPartName.text = part.name
        holder.tvPartPrice.text = "Rp %,.0f".format(part.price) // Format for IDR

        // TODO: Load image using Glide or Picasso
        // For now, use placeholder based on imageName or a default
        val imageResId = context.resources.getIdentifier(part.imageName.substringBefore("."), "drawable", context.packageName)
        if (imageResId != 0) {
            holder.ivPartImage.setImageResource(imageResId)
        } else {
            holder.ivPartImage.setImageResource(R.drawable.placeholder_part) // Default placeholder
        }


        holder.btnViewDetails.setOnClickListener {
            val intent = Intent(context, SparePartDetailActivity::class.java)
            intent.putExtra("PART_ID", part.id)
            intent.putExtra("PART_NAME", part.name) // Pass name for toolbar title
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = partList.size

    fun updateParts(newParts: List<SparePart>) {
        partList = newParts
        notifyDataSetChanged()
    }

    class SparePartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPartImage: ImageView = itemView.findViewById(R.id.ivPartImage)
        val tvPartName: TextView = itemView.findViewById(R.id.tvPartName)
        val tvPartPrice: TextView = itemView.findViewById(R.id.tvPartPrice)
        val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)
    }
}