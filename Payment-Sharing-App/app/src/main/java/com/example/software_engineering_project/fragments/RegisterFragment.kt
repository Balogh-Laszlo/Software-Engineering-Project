package com.example.software_engineering_project.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.software_engineering_project.LoginActivity
import com.example.software_engineering_project.R
import com.example.software_engineering_project.activityResult.PickPhoto


class RegisterFragment : Fragment() {

    companion object{
        const val TAG = "REGISTER FRAGMENT"
    }

    private lateinit var etUserName :EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword:EditText
    private lateinit var etPasswordConfirmation:EditText
    private lateinit var ivRegisterPortrait: ImageView
    private lateinit var rlRegisterPortraitLayout: RelativeLayout
    private lateinit var btnRegister:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        initializeView(view)
        Glide
            .with(requireContext())
            .load(R.drawable.portrait2)
            .circleCrop()
            .into(ivRegisterPortrait)
        setOnClickListeners()
        return view
    }
    private fun initializeView(view: View?) {
        if(view != null){
            etUserName = view.findViewById(R.id.etUserNameRegister)
            etEmail = view.findViewById(R.id.etEmailRegister)
            etPassword = view.findViewById(R.id.etPasswordRegister)
            etPasswordConfirmation = view.findViewById(R.id.etPasswordConfirmationRegister)
            ivRegisterPortrait = view.findViewById(R.id.ivRegisterPortrait)
            rlRegisterPortraitLayout = view.findViewById(R.id.rlRegister)
            btnRegister = view.findViewById(R.id.btnRegisterRegister)
        }
    }

    private fun setOnClickListeners() {
        rlRegisterPortraitLayout.setOnClickListener {
            launchIntentForPhoto()
        }
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_registrationSuccessfulFragment)
        }
    }

    private fun launchIntentForPhoto() {
        if (isReadExternalPermissionGranted()) {
            getPhoto.launch(0)
        } else {
            requestReadExternalPermission()
        }
    }
    private val getPhoto = registerForActivityResult(PickPhoto()) { selectedUri ->
        if (selectedUri != null) {
            Glide
                .with(requireContext())
                .load(selectedUri)
                .circleCrop()
                .into(ivRegisterPortrait)
        }
    }

    private fun isReadExternalPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadExternalPermission() {
        val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(requireActivity(), permission,
            LoginActivity.EXTERNAL_PERMISSION_CODE
        )
    }

}