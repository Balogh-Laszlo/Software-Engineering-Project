package com.example.software_engineering_project.utils

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var userIdForProfile:String = "93wKhX3iB3cLKvP7D1do6K0JnYx1"

}
data class UserData(
    val userId:String,
    val userName:String,
    val userPhoto:String
)