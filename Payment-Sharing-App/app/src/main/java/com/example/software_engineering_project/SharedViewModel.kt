package com.example.software_engineering_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel() {
    val selectedPartyID= MutableLiveData(0)
}