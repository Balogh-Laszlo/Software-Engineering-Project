package com.example.software_engineering_project.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.software_engineering_project.R
import com.example.software_engineering_project.utils.SpecificItem

class ItemListAdapterDialog(val context: Context, private val  items: MutableList<SpecificItem>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ItemListAdapterDialog.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivItemImageDialog)
        private val name = itemView.findViewById<TextView>(R.id.tvItemNameDialog)

        override fun onClick(p0: View?) {
            val currentPosition = this.adapterPosition
            itemClickListener.onItemClick(currentPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(position: Int) {
            if(items[position].item_photo.isNotEmpty()){
                Glide.with(context)
                    .load(items[position].item_photo)
                    .circleCrop()
                    .into(ivImage)
            }
            name.text = items[position].item_name
        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("xxx",items.size.toString())
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

}
