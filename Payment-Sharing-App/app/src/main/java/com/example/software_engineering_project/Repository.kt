package com.example.software_engineering_project

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.software_engineering_project.utils.Item
import com.example.software_engineering_project.utils.Member
import com.example.software_engineering_project.utils.Party
import com.example.software_engineering_project.utils.SpecificItem
import com.google.firebase.firestore.FirebaseFirestore


object Repository {
    private lateinit var party:Party
    private var isReady = 0
    private fun getMembersData(
        partyMembers: MutableList<String>,
        context: Context,
        db: FirebaseFirestore,
        sharedViewModel: SharedViewModel) {
        val members = mutableListOf<Member>()
        db.collection("User Data")
            .whereIn("user_id",partyMembers)
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

                sharedViewModel.partyMembers.value = members
                ready(sharedViewModel)

            }
            .addOnFailureListener {
                Toast.makeText(context,"Something went wrong. Try again later!", Toast.LENGTH_SHORT).show()
            }
    }

    fun trackPartyData(
        context: Context,
        db: FirebaseFirestore,
        sharedViewModel: SharedViewModel,
        viewLifecycleOwner: LifecycleOwner
    ) {
        sharedViewModel.selectedPartyID.observe(viewLifecycleOwner, {
            db.collection("Party")
                .whereEqualTo("party_id", sharedViewModel.selectedPartyID.value)
                .addSnapshotListener { value, error ->
                    Log.d("xxx", "Snapshot" + value!!.documents[0].data!!["password"].toString())
                    party = Party(
                        value!!.documents[0].data!!["is_active"] as Boolean,
                        (value!!.documents[0].data!!["party_id"] as Number).toInt(),
                        value!!.documents[0].data!!["party_name"].toString(),
                        value!!.documents[0].data!!["password"].toString(),
                        (value!!.documents[0].data!!["sum"] as Number).toDouble(),
                        value!!.documents[0].data!!["party_members"] as MutableList<String>,
                        value!!.documents[0].data!!["party_items"] as MutableList<Int>,
                        value!!.documents[0].data!!["item_count"] as MutableList<Int>,
                        value!!.documents[0].data!!["item_price"] as MutableList<Double>
                    )
                    sharedViewModel.party.value = party
                    getMembersData(party.party_members,context,db,sharedViewModel)
                    getPartyItems(party.party_items,context,db, party,sharedViewModel)
                    ready(sharedViewModel)
                }
        })

    }

    private fun getPartyItems(
        partyItems: MutableList<Int>,
        context: Context,
        db: FirebaseFirestore,
        party: Party,
        sharedViewModel: SharedViewModel
    ) {
        var i = 0
        val items = mutableListOf<Item>()
        Log.d("xxx","items"+partyItems.toString())
        for (i in 0 until partyItems.size) {
            db.collection("Item")
                .whereEqualTo("item_id", partyItems[i])
                .get()
                .addOnSuccessListener {
                    Log.d("xxx",it.documents!!.toString())
                    if (party!!.item_count.size >= i) {
                        items.add(
                            Item(
                                it.documents!![0].data!!["item_name"].toString(),
                                (it.documents!![0].data!!["item_id"] as Number).toInt(),
                                it.documents!![0].data!!["item_photo"].toString(),
                                party!!.item_count[i],
                                party!!.item_price[i]
                            )
                        )
                    }
                    Log.d("xxx", items.toString())
                    sharedViewModel.partyItems.value= items
                    ready(sharedViewModel)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Something went wrong. Try again later!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun getItems(
        context: Context,
        type: String,
        db: FirebaseFirestore,
        sharedViewModel: SharedViewModel
    ) {
        val list = mutableListOf<SpecificItem>()
        db.collection("Item")
            .whereEqualTo("type",type)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    list.add(
                        SpecificItem(
                            document.data["item_name"].toString(),
                            (document.data["item_id"] as Number).toInt(),
                            document.data["item_photo"].toString()
                        )
                    )
                }
                sharedViewModel.items.value = list
            }
            .addOnFailureListener {
                Toast.makeText(context,"Something went wrong. Try again later!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun ready(sharedViewModel: SharedViewModel){
        sharedViewModel.isReady.value = false
        if( isReady == 2){
            sharedViewModel.isReady.value = true
            isReady = 0
        }
        else{
            isReady++
        }
    }
}