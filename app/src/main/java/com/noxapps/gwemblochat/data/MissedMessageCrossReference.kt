package com.noxapps.gwemblochat.data

import androidx.room.Entity

@Entity(primaryKeys = ["chatId", "messageId"])
data class MissedMessageCrossReference(
    val chatId:Int,
    val messageId:Int,
) {
}
