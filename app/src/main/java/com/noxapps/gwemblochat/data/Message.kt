package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.security.PublicKey
import java.time.LocalDate
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Entity
data class Message(
    @PrimaryKey val messageId: String = UUID.randomUUID().toString(),
    val remoteId: String = "",
    val recipientId: String = "",
    val sender: String = "", //remove?
    val messageNum: Int = 0,
    val cypherText: String = "",
    val plainText: String = "",
    var _dhPublicKey: String = "",
    val chainLength: Int = 0,
) {
    @OptIn(ExperimentalEncodingApi::class)
    var dhPublicKey: PublicKey
        @Exclude get() {return _dhPublicKey}
        set(value) {_dhPublicKey = Base64.encode(value.encoded).toString()}//value.toString()}

    constructor(oldMessage: Message) : this(
        remoteId = oldMessage.remoteId,
        recipientId = oldMessage.recipientId,
        sender = oldMessage.sender,
        messageNum = oldMessage.messageNum,
    )
}