package com.example.software_engineering_project.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.software_engineering_project.R


class RegistrationSuccessfulFragment : Fragment() {

    private lateinit var btnLogin: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_registration_successful, container, false)
        initializeView(view)
        setOnClickListeners()
        return view
    }

    private fun setOnClickListeners() {
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registrationSuccessfulFragment_to_loginFragment)
        }
    }

    private fun initializeView(view: View?) {
            if(view != null){
                btnLogin = view.findViewById(R.id.btnLoginSeccessful)
            }
    }


}