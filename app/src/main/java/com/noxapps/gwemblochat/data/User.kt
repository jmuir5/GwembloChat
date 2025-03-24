package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User(
    @PrimaryKey val userId:String="",
    val email: String,
    var userName: String,
    //var messages: List<Message>,
    var profilePic:String
) {
}