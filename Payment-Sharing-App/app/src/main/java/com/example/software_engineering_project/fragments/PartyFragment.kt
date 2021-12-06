package com.example.software_engineering_project.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.software_engineering_project.adapters.ItemAdapter
import com.example.software_engineering_project.R
import com.example.software_engineering_project.SharedViewModel
import com.example.software_engineering_project.adapters.MemberAdapter
import com.example.software_engineering_project.utils.Item
import com.example.software_engineering_project.utils.Member
import com.example.software_engineering_project.utils.Party
import com.example.software_engineering_project.utils.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PartyFragment : Fragment() {
    private lateinit var btnSplitBills: Button
    private lateinit var rvMembers:RecyclerView
    private lateinit var rvItems:RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var btnAddItem: ImageButton

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private val db = Firebase.firestore
    private var party: Party? = null
    private val items = mutableListOf<Item>()
    private val members = mutableListOf<Member>()

    private var isReady = 0

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
        sharedViewModel.selectedPartyID.value = 1
        sharedViewModel.selectedPartyID.observe(viewLifecycleOwner,{
            getPartyData(it)

        })

        return view
    }

    private fun getPartyData(partyID: Int?) {
        if(partyID != null){
            db.collection("Party")
                .whereEqualTo("party_id",partyID)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        Log.d("xxx",document.id+document.data)
                        party = Party(document.data["is_active"] as Boolean,
                            (document.data["party_id"] as Number).toInt(),
                            document.data["party_name"].toString(),
                            document.data["password"].toString(),
                            (document.data["sum"] as Number).toDouble(),
                            document.data["party_members"] as MutableList<String>,
                            document.data["party_items"] as MutableList<Int>,
                            document.data["item_count"] as MutableList<Int>,
                            document.data["item_price"] as MutableList<Double>
                            )
                    }
                    ready()
                    getMembersData(party!!.party_members)
                    getItemsData(party!!.party_items)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Something went wrong. Try again later!",Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getItemsData(partyItems: MutableList<Int>) {
        var i = 0
        db.collection("Item")
            .whereIn("item_id", partyItems)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    if (party!!.item_count.size >= i) {
                        items.add(
                            Item(
                                document.data["item_name"].toString(),
                                (document.data["item_id"] as Number).toInt(),
                                document.data["item_photo"].toString(),
                                party!!.item_count[i],
                                party!!.item_price[i]
                            )
                        )
                    }
                    ++i
                }

                Log.d("xxx", items.toString())
                ready()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong. Try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun getMembersData(partyMembers: MutableList<String>) {
        db.collection("User Data")
            .whereIn("user_id",party!!.party_members)
            .get()
            .addOnSuccessListener {
                Log.d("xxx","Success")
                for (document in it) {
                    if (document.data["photo"] != null && document.data["photo"].toString()
                            .isNotEmpty()
                    ) {
                        members.add(
                            Member(
                                document.data["user_id"].toString(),
                                document.data["user_name"].toString(),
                                document.data["photo"].toString()
                            )
                        )
                    }else{
                        members.add(
                            Member(
                                document.data["user_id"].toString(),
                                document.data["user_name"].toString(),
                                ""
                            )
                        )
                    }
                }
                Log.d("xxx",members.toString())
                ready()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Something went wrong. Try again later!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun registerAdapters() {
//        memberAdapter = MemberAdapter(listOf(User("laco.balogh@yahoo.com",
//            "93wKhX3iB3cLKvP7D1do6K0JnYx1",
//        "Laci",
//        "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/userPhotos%2Flaco.balogh%40yahoo.com?alt=media&token=0a05eb48-7235-4c23-8e3e-da42908cb6dd")
//        ),requireContext())
        memberAdapter = MemberAdapter(members,requireContext())
        rvMembers.adapter = memberAdapter
        rvMembers.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

//        itemAdapter = ItemAdapter(requireContext(), listOf(Item("Beer",
//            1,
//            "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/itemPhotos%2Fbeer.jpg?alt=media&token=a9887272-6666-422f-93d6-99945532b214",
//            2,4.5),
//            Item("Pálinka",
//            2,
//            "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/itemPhotos%2Fpalinka.jpg?alt=media&token=8acaab97-d773-429d-93fc-3f811d318546",
//            3,12.5)))
        itemAdapter = ItemAdapter(requireContext(),items)
        rvItems.adapter = itemAdapter
        rvItems.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }

    private fun registerListeners() {
        btnSplitBills.setOnClickListener {
            splitBills()
        }
        btnAddItem.setOnClickListener {
            addItem()
        }
    }

    private fun addItem() {
        Log.d("xxx","Add item clicked")
        val dialog = Dialog(requireContext(),R.style.DialogStyle)
        dialog.setContentView(R.layout.item_type_dialog_layout)

        dialog.window!!.setBackgroundDrawableResource(R.drawable.bg_dialog)

        val btnDrink = dialog.findViewById<ImageView>(R.id.ivDrink)
        Glide.with(requireContext())
            .load(R.drawable.drinks)
            .into(btnDrink)

        val btnFood = dialog.findViewById<ImageView>(R.id.ivFood)
        Glide.with(requireContext())
            .load(R.drawable.food4)
            .into(btnFood)

        val btnOther = dialog.findViewById<ImageView>(R.id.ivOther)
        Glide.with(requireContext())
            .load(R.drawable.other)
            .into(btnOther)
        dialog.show()
    }

    private fun splitBills() {
        TODO("Not yet implemented")
    }

    private fun initializeView(view: View?) {
        if(view != null){
            btnSplitBills = view.findViewById(R.id.btnSplitBills)
            rvItems = view.findViewById(R.id.rvItems)
            rvMembers = view.findViewById(R.id.rvMembers)
            btnAddItem = view.findViewById(R.id.btnAddItem)
        }
    }
    private fun ready(){
        if( isReady == 2){
            registerAdapters()
        }
        else{
            isReady++
        }
    }
}