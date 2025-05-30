package com.example.bengkelgo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bengkelgo.R
import com.example.bengkelgo.SparePart // Pastikan kelas data SparePart Anda sesuai
import com.example.bengkelgo.SparePartDetailActivity

class SparePartAdapter(
    private val context: Context,
    private var partList: List<SparePart>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() { // Ubah ke RecyclerView.ViewHolder

    companion object {
        private const val VIEW_TYPE_FEATURED = 1
        private const val VIEW_TYPE_GRID = 2
    }

    // Asumsi: Anda akan menandai item mana yang featured di data source Anda
    // atau memiliki logika untuk menentukan ini (misalnya, item pertama adalah featured)
    override fun getItemViewType(position: Int): Int {
        // Contoh sederhana: item pertama adalah featured, sisanya grid
        // Anda mungkin perlu logika yang lebih canggih di sini
        // atau tambahkan properti 'isFeatured' pada model SparePart Anda.
        return if (position == 0 && partList.isNotEmpty()) { // Hanya jika ada item, dan item pertama
            VIEW_TYPE_FEATURED
        } else {
            VIEW_TYPE_GRID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_FEATURED) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_spare_part_featured, parent, false)
            FeaturedViewHolder(view)
        } else { // VIEW_TYPE_GRID
            val view = LayoutInflater.from(context).inflate(R.layout.item_spare_part_grid, parent, false)
            GridViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val part = partList[position]
        if (holder is FeaturedViewHolder) {
            holder.bind(part)
        } else if (holder is GridViewHolder) {
            // Jika Anda menggunakan dua RecyclerView terpisah,
            // maka item pertama (featured) akan dihandle oleh adapter/RecyclerView lain.
            // Jadi, jika adapter ini HANYA untuk grid, maka tidak perlu cek VIEW_TYPE_FEATURED di sini.
            // Namun, jika satu adapter untuk semua, maka penyesuaian posisi mungkin diperlukan jika item featured
            // adalah bagian dari `partList` yang sama.
            // Untuk contoh ini, kita asumsikan `partList` bisa berisi item featured (di posisi 0)
            // dan item grid setelahnya.

            // Jika adapter ini hanya untuk grid (dan item featured di-skip atau ditangani adapter lain):
            // val gridPart = partList[position] // Jika partList hanya berisi item grid
            // holder.bind(gridPart)

            // Jika adapter ini menangani semua, dan posisi 0 adalah featured:
            // Jika viewType adalah GRID, maka part yang di-bind adalah partList[position]
            holder.bind(part)
        }
    }

    override fun getItemCount(): Int = partList.size

    fun updateParts(newParts: List<SparePart>) {
        partList = newParts
        notifyDataSetChanged() // Pertimbangkan DiffUtil untuk performa lebih baik
    }

    // ViewHolder untuk item Featured
    inner class FeaturedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPartName: TextView = itemView.findViewById(R.id.tvFeaturedPartName)
        private val ivPartImage: ImageView = itemView.findViewById(R.id.ivFeaturedPartImage)
        private val layoutShopNow: LinearLayout = itemView.findViewById(R.id.layoutShopNow) // atau View jika hanya untuk klik

        fun bind(part: SparePart) {
            tvPartName.text = part.name
            // TODO: Load part.imageName ke ivPartImage menggunakan Glide/Picasso
            // Contoh: Glide.with(context).load(part.imageUrl atau referensi drawable dari imageName).into(ivPartImage)
            val imageResId = context.resources.getIdentifier(part.imageName.substringBefore("."), "drawable", context.packageName)
            if (imageResId != 0) {
                ivPartImage.setImageResource(imageResId)
            } else {
                // Set placeholder jika gambar tidak ditemukan
                ivPartImage.setImageResource(R.drawable.logo) // Ganti dengan placeholder yang sesuai
            }


            itemView.setOnClickListener {
                val intent = Intent(context, SparePartDetailActivity::class.java)
                intent.putExtra("PART_ID", part.id)
                context.startActivity(intent)
            }
        }
    }

    // ViewHolder untuk item Grid
    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPartName: TextView = itemView.findViewById(R.id.tvGridPartName)
        private val tvPartPrice: TextView = itemView.findViewById(R.id.tvGridPartPrice)
        private val ivPartImage: ImageView = itemView.findViewById(R.id.ivGridPartImage)

        fun bind(part: SparePart) {
            tvPartName.text = part.name
            tvPartPrice.text = "Rp %,.0f".format(part.price) // Atau format USD jika perlu
            // TODO: Load part.imageName ke ivPartImage menggunakan Glide/Picasso
            val imageResId = context.resources.getIdentifier(part.imageName.substringBefore("."), "drawable", context.packageName)
            if (imageResId != 0) {
                ivPartImage.setImageResource(imageResId)
            } else {
                ivPartImage.setImageResource(R.drawable.logo) // Ganti dengan placeholder
            }

            itemView.setOnClickListener {
                val intent = Intent(context, SparePartDetailActivity::class.java)
                intent.putExtra("PART_ID", part.id)
                context.startActivity(intent)
            }
        }
    }
}