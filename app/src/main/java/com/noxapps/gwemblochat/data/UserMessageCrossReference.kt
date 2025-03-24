package com.noxapps.gwemblochat.data

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "messageId"])
data class UserMessageCrossReference(
    val userId:Int,
    val messageId:Int,
) {
}
