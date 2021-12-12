package com.example.software_engineering_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.software_engineering_project.utils.Item
import com.example.software_engineering_project.utils.Member
import com.example.software_engineering_project.utils.Party
import com.example.software_engineering_project.utils.SpecificItem

class SharedViewModel:ViewModel() {
    val selectedPartyID= MutableLiveData(0)
    val party = MutableLiveData<Party>()
    val partyItems = MutableLiveData<MutableList<Item>>()
    val partyMembers = MutableLiveData<MutableList<Member>>()
    val items = MutableLiveData<MutableList<SpecificItem>>()
    val isReady = MutableLiveData<Boolean>(false)
    val itemCreationIsReady = MutableLiveData<Boolean>(false)
    val isSubscribed = MutableLiveData(mutableListOf<Boolean>())
    val joinWasSuccessful = MutableLiveData<Boolean>(false)
    val newPartyId = MutableLiveData<Int>(-1)
    val partyCreationWasSuccessful = MutableLiveData<Boolean>(false)
}