package com.example.software_engineering_project.fragments


import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.software_engineering_project.LoginActivity
import com.example.software_engineering_project.R
import com.example.software_engineering_project.activityResult.PickPhoto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


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

    private var imageUri:Uri? = null
    private lateinit var userName: String
    private lateinit var emailAddress: String
    private lateinit var password:String

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val db = Firebase.firestore
//    private val mLoadingBar:ProgressDialog = ProgressDialog(requireContext())



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
            validateData()

        }
    }

    private fun validateData() {
        userName = etUserName.text.toString()
        emailAddress = etEmail.text.toString()
        password = etPassword.text.toString()
        val confirmPassword = etPasswordConfirmation.text.toString()

        if(userName.length <3){
            showError("User name need to be at least 3 character!")
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() ){
            showError("Invalid email address!")
        }
        else if (password.length < 6 ){
            showError("Password need to be at least 6 character long!")
        }
        else if (password != confirmPassword){
            showError("The password and the password confirmation does not match!")
        }
        else{
//            mLoadingBar.setTitle("Registration")
//            mLoadingBar.setMessage("Please wait")
//            mLoadingBar.setCanceledOnTouchOutside(false)
//            mLoadingBar.show()

            mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener { result ->
                if(result.isSuccessful){
                    findNavController().navigate(R.id.action_registerFragment_to_registrationSuccessfulFragment)
                    mAuth.currentUser?.let { it.sendEmailVerification().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            showError("Email sent to $emailAddress")
                        }
                    }
                    }

                }
                else{
                    Toast.makeText(requireContext(),"${result.result}",Toast.LENGTH_LONG).show()
                }
                if(imageUri != null) {
                    uploadImage()
                }
                else{
                    saveUserDataToFirestore()
                }
            }

        }
    }

    private fun uploadImage() {
        if(mAuth.currentUser != null){
            val path = storage.reference.child("userPhotos").child(emailAddress)
            path.putFile(imageUri!!).addOnSuccessListener {
                Toast.makeText(requireContext(),"Photo uploaded",Toast.LENGTH_SHORT).show()
                path.downloadUrl.addOnSuccessListener { uri ->
                    saveUserDataToFirestore(uri)
                }
            }

        }
        else{
            saveUserDataToFirestore()
        }
    }
    private fun saveUserDataToFirestore(){
        val doc = db.collection("User Data").document(emailAddress)
        val data = hashMapOf(
            "user_id" to mAuth.currentUser!!.uid,
            "user_name" to userName
        )
        doc.set(data).addOnCompleteListener {
            if(it.isSuccessful){
                showError("Your data has been uploaded!")
            }
            else{
                showError("Failed to upload your data!")
            }
        }
    }

    private fun saveUserDataToFirestore(uri: Uri?) {
        val doc = db.collection("User Data").document(emailAddress)
        val data = hashMapOf(
            "user_id" to mAuth.currentUser!!.uid,
            "photo" to uri.toString(),
            "user_name" to userName
        )
        doc.set(data).addOnCompleteListener {
            if(it.isSuccessful){
                showError("Your data has been uploaded!")
            }
            else{
                showError("Failed to upload your data!")
            }
        }
    }


    private fun showError(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
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
            imageUri = selectedUri
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