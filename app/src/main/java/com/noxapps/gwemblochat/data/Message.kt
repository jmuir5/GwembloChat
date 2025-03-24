package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.time.LocalDate

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)val messageId: Int,
    val conversationId: Int,
    val sender: Int,
    val message: String,
    var _timestamp: String = ""
) {
    var timestamp:LocalDate
        @Exclude get() {return LocalDate.parse(_timestamp)}
        set(value) {
            _timestamp = value.toString()
        }
}