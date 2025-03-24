package com.noxapps.gwemblochat.data

import androidx.room.Embedded
import androidx.room.Relation

class Relationships {
    data class UserWithMessages(
        @Embedded val user: User,
        @Relation(
            parentColumn = "userId",
            entityColumn = "chatId",
        )
        val gifts:List<Message>
    )
}