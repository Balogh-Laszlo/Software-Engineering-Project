package com.example.software_engineering_project.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.software_engineering_project.R
import com.google.android.material.button.MaterialButtonToggleGroup


class LoginFragment : Fragment() {

    companion object{
        const val TAG = "LOGIN FRAGMENT"
    }

    private lateinit var ivLoginPortrait : ImageView
    private lateinit var btnLogin : Button
    private lateinit var btnRegister : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.i(TAG,"Login Fragment created")
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        initializeView(view)
        steOnClickListeners()
        Glide
            .with(requireContext())
            .load(R.drawable.portrait2)
            .circleCrop()
            .into(ivLoginPortrait)

        return view
    }

    private fun steOnClickListeners() {
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun initializeView(view: View?) {
        if(view != null){
            ivLoginPortrait = view.findViewById(R.id.ivLoginPortrait)
            btnRegister = view.findViewById(R.id.btnRegisterLogin)
            btnLogin = view.findViewById(R.id.btnLogin)
        }
    }


}