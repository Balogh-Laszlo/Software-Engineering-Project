package com.example.software_engineering_project.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.software_engineering_project.R
import com.example.software_engineering_project.utils.Party

class PartyListAdapter(private val context: Context,
                       private var partyList: ArrayList<Party>,
                       private val listener: OnItemClickListener) :
    RecyclerView.Adapter<PartyListAdapter.PartyListViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    inner class PartyListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val ivOwnerPicture: ImageView = itemView.findViewById(R.id.partyList_ivOwnerPicture)
        val tvPartyName: TextView = itemView.findViewById(R.id.partyList_tvPartyName)
        val tvTotal: TextView = itemView.findViewById(R.id.partyList_tvTotal)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val pos = this.adapterPosition
            listener.onItemClick(pos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyListViewHolder {
        Log.d("xxx","onCreateViewHolder() - PartyListViewHolder")
        val itemView = LayoutInflater.from(context).inflate(R.layout.party_list_item_layout, parent, false)
        return PartyListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PartyListViewHolder, position: Int) {
        val currentParty = partyList[position]
        Log.d("xxx","onBindViewHolder() - currentParty: $currentParty")
        holder.tvPartyName.text = currentParty.party_name
        holder.tvTotal.text = currentParty.sum.toString()
        Glide.with(this.context)
            .load(R.drawable.ic_outline_person_24)
            .into(holder.ivOwnerPicture)
    }

    override fun getItemCount(): Int = partyList.size

    fun setData(newList: ArrayList<Party>) {
        partyList = newList
    }
}