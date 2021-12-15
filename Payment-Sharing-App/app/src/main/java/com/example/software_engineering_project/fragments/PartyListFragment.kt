package com.example.software_engineering_project.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.software_engineering_project.MyApplication
import com.example.software_engineering_project.R
import com.example.software_engineering_project.SharedViewModel
import com.example.software_engineering_project.adapters.PartyListAdapter
import com.example.software_engineering_project.utils.Party
import com.example.software_engineering_project.utils.SpecificItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [PartyListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PartyListFragment : Fragment(), PartyListAdapter.OnItemClickListener {

    private lateinit var rvPartyList: RecyclerView
    private lateinit var adapter: PartyListAdapter
    private val db = Firebase.firestore
    private val parties = MutableLiveData<MutableList<Party>>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_party_list, container, false)
        view?.apply {
            rvPartyList = view.findViewById(R.id.partyList_rvPartyList)
            setupRecyclerView()
            parties.observe(viewLifecycleOwner) {
                adapter.setData(parties.value as ArrayList<Party>)
                adapter.notifyDataSetChanged()
            }
            loadParties()
        }
        return view
    }

    private fun setupRecyclerView() {
        adapter = PartyListAdapter(requireContext(), ArrayList<Party>(), this)
        rvPartyList.adapter = adapter
        rvPartyList.layoutManager = LinearLayoutManager(this.context)
        rvPartyList.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        rvPartyList.setHasFixedSize(true)
    }

    private fun loadParties() {
        parties.value = mutableListOf<Party>()
        db.collection("Party").get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val party = Party(document.data["is_active"] as Boolean,
                        (document.data["party_id"] as Number).toInt(),
                        document.data["party_name"].toString(),
                        document.data["password"].toString(),
                        (document.data["sum"] as Number).toDouble(),
                        document.data["party_members"] as MutableList<String>,
                        document.data["party_items"] as MutableList<Int>,
                        document.data["item_count"] as MutableList<Int>,
                        document.data["item_price"] as MutableList<Double>
                    )
                    if (party.party_members.contains(MyApplication.UID)) {
                        parties.value!!.add(party)
                    }
                }
                adapter.setData(parties.value as ArrayList<Party>)
                adapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        sharedViewModel.selectedPartyID.value = parties.value?.get(position)?.party_id
        findNavController().navigate(R.id.action_partyListFragment_to_partyFragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PartyListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PartyListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}