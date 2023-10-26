package com.example.firebasefirestorecrudapp

import com.google.firebase.Timestamp

data class Data(
    var id:String?=null,
    val name:String?=null,
    val email:String?=null,
    val sub:String?=null,
    val birthDay:Int?=null,
    val timestamp: Timestamp?= null
)
