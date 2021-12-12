package com.example.software_engineering_project.fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.software_engineering_project.adapters.ItemAdapter
import com.example.software_engineering_project.R
import com.example.software_engineering_project.Repository
import com.example.software_engineering_project.SharedViewModel
import com.example.software_engineering_project.activityResult.PickPhoto
import com.example.software_engineering_project.adapters.ItemListAdapterDialog
import com.example.software_engineering_project.adapters.MemberAdapter
import com.example.software_engineering_project.utils.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class PartyFragment : Fragment(), ItemListAdapterDialog.OnItemClickListener,
    ItemAdapter.OnSubscribeClickListener {
    private lateinit var btnSplitBills: Button
    private lateinit var rvMembers:RecyclerView
    private lateinit var rvItems:RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var btnAddItem: ImageButton
    private lateinit var tvSum: TextView
    private lateinit var ivPhoto: ImageView

    private var itemPhotoUri: Uri? = null

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()


    private var type =""

    private lateinit var dialog: Dialog
    private lateinit var selectedItemByDialog: SpecificItem
    private lateinit var selectedItemFinal: Item

    private var itemName: String =""



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
        Repository.trackPartyData(requireContext(),db,sharedViewModel,viewLifecycleOwner)
        sharedViewModel.isReady.observe(viewLifecycleOwner){
            if (it){
                registerAdapters()
                tvSum.text = sharedViewModel.party.value!!.sum.toString()+" RON"
            }
        }

        return view
    }

    private fun registerAdapters() {
//        memberAdapter = MemberAdapter(listOf(User("laco.balogh@yahoo.com",
//            "93wKhX3iB3cLKvP7D1do6K0JnYx1",
//        "Laci",
//        "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/userPhotos%2Flaco.balogh%40yahoo.com?alt=media&token=0a05eb48-7235-4c23-8e3e-da42908cb6dd")
//        ),requireContext())
        if(sharedViewModel.partyMembers.value != null) {
            memberAdapter = MemberAdapter(sharedViewModel.partyMembers.value!!, requireContext())
            rvMembers.adapter = memberAdapter
            rvMembers.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

//        itemAdapter = ItemAdapter(requireContext(), listOf(Item("Beer",
//            1,
//            "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/itemPhotos%2Fbeer.jpg?alt=media&token=a9887272-6666-422f-93d6-99945532b214",
//            2,4.5),
//            Item("Pálinka",
//            2,
//            "https://firebasestorage.googleapis.com/v0/b/payment-sharing-app.appspot.com/o/itemPhotos%2Fpalinka.jpg?alt=media&token=8acaab97-d773-429d-93fc-3f811d318546",
//            3,12.5)))
        itemAdapter = ItemAdapter(requireContext(),sharedViewModel.partyItems.value!!,this,sharedViewModel,viewLifecycleOwner,db)
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

        setDialogListeners(btnDrink,btnFood,btnOther,dialog)
        dialog.show()
    }

    private fun setDialogListeners(
        btnDrink: ImageView,
        btnFood: ImageView,
        btnOther: ImageView,
        dialog:Dialog
    ) {
        btnDrink.setOnClickListener {
            type = "drink"
            dialog.dismiss()
            itemList()
        }
        btnFood.setOnClickListener {
            type = "food"
            dialog.dismiss()
            itemList()
        }
        btnOther.setOnClickListener {
            type = "other"
            dialog.dismiss()
            itemList()
        }
    }

    private fun itemList() {
        dialog = Dialog(requireContext(),R.style.DialogStyle)
        dialog.setContentView(R.layout.item_list_dialog_layout)

        dialog.window!!.setBackgroundDrawableResource(R.drawable.bg_dialog)

        val rvItemList = dialog.findViewById<RecyclerView>(R.id.rvItemListDialog)
        val btnNewItem = dialog.findViewById<Button>(R.id.btnNewItemDialog)

        Repository.getItems(requireContext(),type,db,sharedViewModel)
        sharedViewModel.items.observe(viewLifecycleOwner){
            rvItemList.adapter = ItemListAdapterDialog(requireContext(), sharedViewModel.items.value!!,this)
            rvItemList.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
        btnNewItem.setOnClickListener {
            createNewItem()
        }
        dialog.show()
    }

    private fun createNewItem() {
        val createDialog = Dialog(requireContext(),R.style.DialogStyle)
        createDialog.setContentView(R.layout.create_new_item_dialog_layout)
        createDialog.window!!.setBackgroundDrawableResource(R.drawable.bg_dialog)
        val etItemName = createDialog.findViewById<EditText>(R.id.etItemNameDialog)
        ivPhoto = createDialog.findViewById<ImageView>(R.id.ivNewItemPhoto)
        val btnCreate = createDialog.findViewById<Button>(R.id.btnCreateNewItem)
        ivPhoto.setOnClickListener {
            getPhoto.launch(0)
        }
        btnCreate.setOnClickListener {
            if(etItemName.text.isNotEmpty()) {
                itemName = etItemName.text.toString()
                uploadImage()
                createDialog.dismiss()
                sharedViewModel.itemCreationIsReady.observe(viewLifecycleOwner){
                    if(sharedViewModel.itemCreationIsReady.value!!) {
                        itemList()
                        sharedViewModel.itemCreationIsReady.value = false
                    }
                }

            }
        }

        createDialog.show()

    }
    private fun uploadImage() {
        if (itemPhotoUri != null) {
            val path = storage.reference.child("itemPhotos").child(itemName+System.currentTimeMillis().toString())
            path.putFile(itemPhotoUri!!).addOnSuccessListener {
                Toast.makeText(requireContext(), "Photo uploaded", Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener { uri ->
                    Repository.saveItemDataToFirestore(uri,requireContext(),db,itemName,type,sharedViewModel)
                }
            }
        }
    }
    private val getPhoto = registerForActivityResult(PickPhoto()) { selectedUri ->
        if (selectedUri != null) {
            Glide
                .with(requireContext())
                .load(selectedUri)
                .circleCrop()
                .into(ivPhoto)
            itemPhotoUri = selectedUri
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
            btnAddItem = view.findViewById(R.id.btnAddItem)
            tvSum = view.findViewById(R.id.tvTotalAmount)
        }
    }


    override fun onItemClick(position: Int) {
        selectedItemByDialog = sharedViewModel.items.value!![position]
        Log.d("xxx",selectedItemByDialog.toString())
        dialog.dismiss()
        val countAndPriceDialog = Dialog(requireContext(),R.style.DialogStyle)
        countAndPriceDialog.setContentView(R.layout.price_and_count_dialog_layout)
        countAndPriceDialog.window!!.setBackgroundDrawableResource(R.drawable.bg_dialog)
        countAndPriceDialog.show()

        val btnOk = countAndPriceDialog.findViewById<Button>(R.id.btnOkDialog)
        val tvCount = countAndPriceDialog.findViewById<TextView>(R.id.tvCountDialog)
        val tvPrice = countAndPriceDialog.findViewById<TextView>(R.id.tvPriceDialog)
        btnOk.setOnClickListener {
            countAndPriceDialog.dismiss()
            selectedItemFinal = Item(selectedItemByDialog.item_name,selectedItemByDialog.item_id,selectedItemByDialog.item_photo,tvCount.text.toString().toInt(),tvPrice.text.toString().toDouble(),0)
            Repository.addItemToFirebase(requireContext(),selectedItemFinal,db,
                sharedViewModel.party.value!!,sharedViewModel)
        }
        setCountAndPriceListeners(countAndPriceDialog)

    }

    private fun setCountAndPriceListeners(dialog: Dialog) {
        val btnPlusPrice = dialog.findViewById<ImageButton>(R.id.btnPlusPrice)
        val btnMinusPrice = dialog.findViewById<ImageButton>(R.id.btnMinusPrice)
        val btnPlusCount = dialog.findViewById<ImageButton>(R.id.btnPlusCount)
        val btnMinusCount = dialog.findViewById<ImageButton>(R.id.btnMinusCount)
        val tvPrice = dialog.findViewById<TextView>(R.id.tvPriceDialog)
        val tvCount = dialog.findViewById<TextView>(R.id.tvCountDialog)
        if(tvPrice.text == "1"){
            btnMinusPrice.isEnabled = false
        }
        if(tvCount.text == "1"){
            btnMinusCount.isEnabled = false
        }
        btnPlusPrice.setOnClickListener {
            if(tvPrice.text == "1" || tvPrice.text.toString().toInt() == 1 ){
                btnMinusPrice.isEnabled = true
            }
            tvPrice.text = (tvPrice.text.toString().toInt() + 1).toString()
        }
        btnMinusPrice.setOnClickListener {
            if(tvPrice.text == "2" || tvPrice.text.toString().toInt() == 2){
                btnMinusPrice.isEnabled = false
            }
            tvPrice.text = (tvPrice.text.toString().toInt() - 1).toString()
        }
        btnPlusCount.setOnClickListener {
            if(tvCount.text == "1" || tvCount.text.toString().toInt() == 1 ){
                btnMinusCount.isEnabled = true
            }
            tvCount.text = (tvCount.text.toString().toInt() + 1).toString()
        }
        btnMinusCount.setOnClickListener {
            if(tvCount.text == "2" || tvCount.text.toString().toInt() == 2){
                btnMinusCount.isEnabled = false
            }
            tvCount.text = (tvCount.text.toString().toInt() - 1).toString()
        }
    }

    override fun onSubscribeClick(position: Int) {
        Log.d("xxx","Subscribe"+sharedViewModel.partyItems.value!![position].toString())
        Repository.subscribe(requireContext(),sharedViewModel,db,itemAdapter,position)
    }
}