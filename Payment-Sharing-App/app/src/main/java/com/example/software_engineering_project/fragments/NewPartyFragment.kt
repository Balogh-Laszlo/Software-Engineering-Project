package com.example.software_engineering_project.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.software_engineering_project.R
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass.
 * Use the [NewPartyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewPartyFragment : Fragment() {

    private lateinit var etCode: TextInputEditText
    private lateinit var btnJoin: Button
    private lateinit var etPartyName: TextInputEditText
    private lateinit var btnCreate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_party, container, false)
        view?.apply {
            initializeViews(this)
            setOnClickListeners(this)
        }
        return view
    }

    private fun setOnClickListeners(view: View) {
        btnJoin.setOnClickListener {
            Log.d("xxx", "Join button pressed")
            if (etCode.text.isNullOrBlank()) {
                Toast.makeText(view.context, "No code given.", Toast.LENGTH_SHORT).show()
            } else {
                // JOIN PARTY
            }
        }
        btnCreate.setOnClickListener {
            Log.d("xxx", "Create button pressed")
            if (etPartyName.text.isNullOrBlank()) {
                Toast.makeText(view.context, "No party name given.", Toast.LENGTH_SHORT).show()
            } else {
                // CREATE PARTY
            }
        }
    }

    private fun initializeViews(view: View) {
        etCode = view.findViewById(R.id.newparty_etCode)
        btnJoin = view.findViewById(R.id.newparty_btnJoin)
        etPartyName = view.findViewById(R.id.newparty_etPartyName)
        btnCreate = view.findViewById(R.id.newparty_btnCreate)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewPartyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewPartyFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}