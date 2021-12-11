package com.example.software_engineering_project.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.software_engineering_project.R
import com.example.software_engineering_project.utils.Member
import com.example.software_engineering_project.utils.User

class MemberAdapter(
    private val list:List<Member>,
    private val context:Context
):RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    inner class ViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        private val ivMemberImage = itemView.findViewById<ImageView>(R.id.ivMemberImage)
        private val tvMemberName = itemView.findViewById<TextView>(R.id.tvMemberName)
        fun bind(position: Int) {
            tvMemberName.text = list[position].user_name
            if (list[position].user_photo != null && list[position].user_photo.isNotEmpty()){
                Glide.with(context)
                    .load(list[position].user_photo)
                    .circleCrop()
                    .into(ivMemberImage)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_member_layout,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size

}
