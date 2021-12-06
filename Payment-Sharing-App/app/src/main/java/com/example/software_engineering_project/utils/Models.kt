package com.example.software_engineering_project.utils

data class User(val email:String, val userId:String, val userName:String, val userPhoto:String)

data class Item(val item_name:String, val item_id:Int, val item_photo:String, val item_count:Int, val item_price:Double)

data class SpecificItem(val item_name: String, val item_id: Int, val item_photo: String)

data class Party(var is_active:Boolean,
                 val party_id:Int,
                 val party_name:String,
                 val password:String,
                 var sum:Double,
                 val  party_members:MutableList<String>,
                 val party_items:MutableList<Int>,
                 val item_count:MutableList<Int>,
                 val item_price:MutableList<Double>
                 )
data class Member(val user_id:String,
                  val user_name:String,
                  val user_photo:String
                  )