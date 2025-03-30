package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: String = "",
    val partnerId: String = "",
    val lastMessageId: String = UUID.randomUUID().toString(),
    val activated : Boolean=true,

    //user id
    //messages
    //encrypted messages
    //last message timestamp(indexed)
    //last message
) {
    constructor(chat:Chat, messageId: String) : this(
        id = chat.id,
        ownerId = chat.ownerId,
        partnerId = chat.partnerId,
        lastMessageId = messageId

    )
}