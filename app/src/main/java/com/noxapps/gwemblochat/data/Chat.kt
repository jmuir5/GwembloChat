package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: String = "",
    val partnerId: String = "",
    var lastMessageId: String = UUID.randomUUID().toString(),
    //user id
    //messages
    //encrypted messages
    //last message timestamp(indexed)
    //last message

) {
}