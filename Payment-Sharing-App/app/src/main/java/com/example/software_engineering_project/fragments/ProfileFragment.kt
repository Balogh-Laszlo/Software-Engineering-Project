package com.example.software_engineering_project.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.software_engineering_project.R
import com.example.software_engineering_project.utils.SharedViewModel
import com.example.software_engineering_project.utils.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val db = Firebase.firestore
    private val model: SharedViewModel by activityViewModels()
    private var userData: MutableLiveData<UserData> = MutableLiveData()

    private lateinit var tvUserName:TextView
    private lateinit var ivProfile:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(model.userIdForProfile.isEmpty()){
            model.userIdForProfile = mAuth.currentUser!!.uid
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        initializeView(view)
        getUserData()
        userData.observe(viewLifecycleOwner){
            setUserData()
        }
        return view
    }

    private fun setUserData() {
        tvUserName.text = userData.value!!.userName
        if(userData.value!!.userPhoto!=null){
            Glide.with(requireContext())
                .load(userData.value!!.userPhoto)
                .override(200)
                .circleCrop()
                .into(ivProfile)
        }
    }

    private fun getUserData() {
        db.collection("User Data")
            .whereEqualTo("user_id",model.userIdForProfile)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    Log.d("PROFILE", "${document.data["user_id"]}")
                    Log.d("PROFILE", "${document.data["user_name"]}")
                    Log.d("PROFILE", "${document.data["photo"]}")
                    var user:UserData
                    if (document.data["photo"]!=null) {
                         user = UserData(
                            document.data["user_id"].toString(),
                            document.data["user_name"].toString(),
                            document.data["photo"].toString()
                        )
                    }
                    else{
                        user =  UserData(
                            document.data["user_id"].toString(),
                            document.data["user_name"].toString(),
                            ""
                        )
                    }
                    userData.value = user
                }
            }
    }

    private fun initializeView(view: View?) {
        if(view!= null){
            ivProfile = view.findViewById(R.id.ivProfilePictureProfile)
            tvUserName = view.findViewById(R.id.tvUserNameProfile)
        }
    }
}