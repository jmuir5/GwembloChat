package com.noxapps.gwemblochat.data

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.coroutines.flow.Flow

class Relationships {
    /*data class UserWithMessages(
        @Embedded val user: User,
        @Relation(
            parentColumn = "userId",
            entityColumn = "chatId",
        )
        val gifts:List<Message>
    )*/

    data class ChatWithUser(
        @Embedded val chat: Chat,
        @Relation(
            parentColumn = "partnerId",
            entityColumn = "userId",
        )
        val user: User,
    )
    data class ChatWithUserAndLastMessage(
        @Embedded val chat: Chat,
        @Relation(
            parentColumn = "partnerId",
            entityColumn = "userId",
        )
        val user: User,
        @Relation(
            parentColumn = "lastMessageId",
            entityColumn = "messageId",
        )
        val lastMessage: Message,
    )
    data class ChatWithUserAndAllMessages(
        @Embedded val chat: Chat,
        @Relation(
            parentColumn = "partnerId",
            entityColumn = "userId",
        )
        val user: User,
        @Relation(
            parentColumn = "partnerId",
            entityColumn = "remoteId",
        )
        val messages: List<Message>,
    ){
        val missedMessages = messages.filter { it.plainText == "" }
    }


}
