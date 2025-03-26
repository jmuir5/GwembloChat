package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.time.LocalDate
import java.util.UUID

@Entity
data class Message(
    @PrimaryKey val messageId: UUID = UUID.randomUUID(),
    val recipientId: String = "",
    val sender: String = "", //remove?
    val messageNum: Int = 0,
    val message: String = "",
) {

}