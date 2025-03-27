package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.time.LocalDate
import java.util.UUID

@Entity
data class Message(
    @PrimaryKey val messageId: String = UUID.randomUUID().toString(),
    val remoteId: String = "",
    val recipientId: String = "",
    val sender: String = "", //remove?
    val messageNum: Int = 0,
    val message: String = "",
) {
    constructor(oldMessage: Message) : this(
        remoteId = oldMessage.remoteId,
        recipientId = oldMessage.recipientId,
        sender = oldMessage.sender,
        messageNum = oldMessage.messageNum,
    )
}