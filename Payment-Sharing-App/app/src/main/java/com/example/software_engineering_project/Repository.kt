package com.example.software_engineering_project

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.software_engineering_project.adapters.ItemAdapter
import com.example.software_engineering_project.utils.Item
import com.example.software_engineering_project.utils.Member
import com.example.software_engineering_project.utils.Party
import com.example.software_engineering_project.utils.SpecificItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Thread.sleep


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
                                party!!.item_price[i],
                                i
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
            sleep(100)
            sharedViewModel.isReady.value = true
            isReady = 0
        }
        else{
            isReady++
        }
    }

    fun addItemToFirebase(
        context: Context,
        selectedItemFinal: Item,
        db: FirebaseFirestore,
        party: Party,
        sharedViewModel: SharedViewModel
    ){
        val count = party.item_count
        count.add(selectedItemFinal.item_count)
        val price = party.item_price
        price.add(selectedItemFinal.item_price)
        val id = party.party_items
        id.add(selectedItemFinal.item_id)
        val sum = party.sum + selectedItemFinal.item_price

        db.collection("Party")
            .whereEqualTo("party_id",sharedViewModel.selectedPartyID.value)
            .get()
            .addOnSuccessListener {
                db.collection("Party").document(it.documents[0].id)
                    .update(
                        mapOf(
                            "item_count" to count,
                            "item_price" to price,
                            "party_items" to id,
                            "sum" to sum
                        )

                    )
                    .addOnSuccessListener {
                        Toast.makeText(context,"You successfully added an item to the party",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context,"Something went wrong. Try again later!",Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context,"Something went wrong. Try again later!",Toast.LENGTH_SHORT).show()
            }
    }


    fun saveItemDataToFirestore(
        uri: Uri,
        context: Context,
        db: FirebaseFirestore,
        itemName: String,
        type: String,
        sharedViewModel: SharedViewModel
    ) {
        var itemId = 0
        db.collection("Item").get()
            .addOnSuccessListener {
                itemId = (it.documents[it.documents.size-1].data!!["item_id"] as Number).toInt()
                itemId++
                db.collection("Item").document(itemName+System.currentTimeMillis().toString())
                    .set(mapOf(
                        "item_name" to itemName,
                        "item_id" to itemId,
                        "item_photo" to uri.toString(),
                        "type" to type
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(context,"Item creation successful!",Toast.LENGTH_SHORT).show()
                        sharedViewModel.itemCreationIsReady.value = true
                    }
                    .addOnFailureListener {
                        Toast.makeText(context,"Item creation failed!",Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context,"Item creation failed!",Toast.LENGTH_SHORT).show()
            }

    }

    fun checkIfSubscribed(
        position: Int,
        itemList: List<Item>,
        sharedViewModel: SharedViewModel,
        db: FirebaseFirestore
    ) {
        db.collection("Subscribers")
            .whereEqualTo("party_id",sharedViewModel.selectedPartyID.value)
            .whereEqualTo("party_item_index", itemList[position].index)
            .get()
            .addOnSuccessListener {
                Log.d("xxx","subscribers collection: "+it.documents.toString())
                if (it.documents.size < 1){
                    sharedViewModel.isSubscribed.value!![position] = false
                }else {
                    val subscribedUsers =
                        it.documents[0].data!!["subscribed_members_index"] as List<String>
                    if(subscribedUsers.contains(MyApplication.UID)){
                        sharedViewModel.isSubscribed.value!![position] = true
                    }
                }
            }
            .addOnFailureListener {
                Log.d("xxx", "checkIfSubscribed error$it")
            }
    }

    fun subscribe(
        context: Context,
        sharedViewModel: SharedViewModel,
        db: FirebaseFirestore,
        itemAdapter: ItemAdapter,
        position: Int
    ) {
        db.collection("Subscribers")
            .whereEqualTo("party_id", sharedViewModel.selectedPartyID.value)
            .whereEqualTo("party_item_index", sharedViewModel.partyItems.value!![position].index)
            .get()
            .addOnSuccessListener {
                if(it.documents.size>0){
                    db.collection("Subscribers").document(it.documents[0].id)
                        .update("subscribed_members_index", FieldValue.arrayUnion(MyApplication.UID))
                        .addOnSuccessListener {
                            Toast.makeText(context,"You successfully subscribed!",Toast.LENGTH_SHORT).show()
                            itemAdapter.notifyDataSetChanged()
                        }
                }
                else{
                    db.collection("Subscribers").document(sharedViewModel.party.value!!.party_name+System.currentTimeMillis())
                        .set(mapOf(
                            "party_id" to sharedViewModel.party.value!!.party_id,
                            "party_item_index" to sharedViewModel.partyItems.value!![position].index,
                            "subscribed_members_index" to listOf<String>(MyApplication.UID)
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(context,"You successfully subscribed!",Toast.LENGTH_SHORT).show()
                            itemAdapter.notifyDataSetChanged()
                        }
                }
            }

    }

    fun joinParty(password: String, db: FirebaseFirestore, context: Context) {
        db.collection("Party")
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    Log.d("xxx", "party members: ${it.documents[0]["party_members"]}")
                    val members = it.documents[0]["party_members"] as ArrayList<String>
                    if (members.contains(MyApplication.UID)) {
                        // already member
                        Toast.makeText(context,"You are already a member of this party.",Toast.LENGTH_LONG).show()
                    } else {
                        // add as member
                        val docId = it.documents[0].id
                        val partyRef = db.collection("Party").document(docId)
                        partyRef.update("party_members", FieldValue.arrayUnion(MyApplication.UID))
                            .addOnCompleteListener {
                                Toast.makeText(context,"Joined successfully.",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context,"Failed to join party.",Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(context,"Password is not associated with any party.",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context,"Something went wrong.",Toast.LENGTH_SHORT).show()
            }
    }
}