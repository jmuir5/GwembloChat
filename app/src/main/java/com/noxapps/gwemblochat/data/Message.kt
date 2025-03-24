package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.time.LocalDate
import java.util.UUID

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)val messageId: UUID,
    val recipientId: Int,
    val sender: Int, //remove?
    val messageNum: Int,
    val message: String,
) {

}