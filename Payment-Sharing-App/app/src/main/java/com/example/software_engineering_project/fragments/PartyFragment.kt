package com.example.software_engineering_project.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.software_engineering_project.adapters.ItemAdapter
import com.example.software_engineering_project.R
import com.example.software_engineering_project.adapters.MemberAdapter
import com.example.software_engineering_project.utils.User


class PartyFragment : Fragment() {
    private lateinit var btnSplitBills: Button
    private lateinit var rvMembers:RecyclerView
    private lateinit var rvItems:RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_party, container, false)
        initializeView(view)
        registerListeners()
        registerAdapters()
        return view
    }

    private fun registerAdapters() {
        memberAdapter = MemberAdapter(listOf(User("laco.balogh@yahoo.com",
            "93wKhX3iB3cLKvP7D1do6K0JnYx1",
        "Laci",
        "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/userPhotos%2Flaco.balogh%40yahoo.com?alt=media&token=0a05eb48-7235-4c23-8e3e-da42908cb6dd")
        ),requireContext())
        rvMembers.adapter = memberAdapter
        rvMembers.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        itemAdapter = ItemAdapter()
//        rvItems.adapter = itemAdapter
        rvItems.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }

    private fun registerListeners() {
        btnSplitBills.setOnClickListener {
            splitBills()
        }
    }

    private fun splitBills() {
        TODO("Not yet implemented")
    }

    private fun initializeView(view: View?) {
        if(view != null){
            btnSplitBills = view.findViewById(R.id.btnSplitBills)
            rvItems = view.findViewById(R.id.rvItems)
            rvMembers = view.findViewById(R.id.rvMembers)
        }
    }

}