package com.example.software_engineering_project.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.software_engineering_project.R
import com.example.software_engineering_project.utils.Item

class ItemAdapter(
    private val context: Context,
    private val itemList: List<Item>,
    private val listener:OnSubscribeClickListener
) :RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
    interface OnSubscribeClickListener{
        fun onSubscribeClick(position: Int)
    }
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvItemName)
        private val tvCount = itemView.findViewById<TextView>(R.id.tvItemCount)
        private val tvTotalPrice = itemView.findViewById<TextView>(R.id.tvTotalPrice)
        private val ivItemPhoto = itemView.findViewById<ImageView>(R.id.ivItemImage)
        private val btnSubscribe = itemView.findViewById<ImageButton>(R.id.ibtnSubscribe)

        fun bind(position: Int) {
            tvName.text = itemList[position].item_name
            tvCount.text = "x${itemList[position].item_count.toString()}"
            tvTotalPrice.text = "${(itemList[position].item_count*itemList[position].item_price).toString()} RON"
            if (itemList[position].item_photo != null && itemList[position].item_photo.isNotEmpty()){
                Glide.with(context)
                    .load(itemList[position].item_photo)
                    .circleCrop()
                    .into(ivItemPhoto)
            }else{
                Glide.with(context)
                    .load(R.drawable.product)
                    .circleCrop()
                    .into(ivItemPhoto)
            }
            btnSubscribe.setOnClickListener {

                listener.onSubscribeClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_item_layout,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = itemList.size

}
