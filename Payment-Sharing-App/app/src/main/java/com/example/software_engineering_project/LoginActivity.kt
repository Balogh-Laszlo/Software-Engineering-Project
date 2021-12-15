package com.example.software_engineering_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity(){
    companion object{
        const val EXTERNAL_PERMISSION_CODE = 442
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.title = ""

    }
}