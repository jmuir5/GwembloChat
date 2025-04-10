package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.noxapps.gwemblochat.crypto.ECDH
import com.noxapps.gwemblochat.crypto.Header
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndAllMessages
import kotlinx.coroutines.CoroutineScope
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
    val cypherText: ByteArray = byteArrayOf(),
    @Exclude var plainText: String = "",
    @Exclude val messageKey: ByteArray = byteArrayOf(),
    var dhPublicKey: ByteArray = byteArrayOf(),
    val chainLength: Int = 0,
    val messageNum: Int = 0,
) {
    companion object{
        fun pullMessage(
            message: Message,
            chat: ChatWithUserAndAllMessages,
            db: AppDatabase,
            associatedData:ByteArray,
            coroutineScope: CoroutineScope
        ): Message{
            val decryptedMessage = ECDH.ratchetDecrypt(
                chat,
                Header(message.dhPublicKey, message.chainLength, message.messageNum),
                message.cypherText,
                associatedData,
                db,
                coroutineScope
            )
            val messageKey = ECDH.kdfCK(chat.chat.sentChainKey).second
            return Message(
                remoteId = message.remoteId,
                recipientId = message.recipientId,
                sender = message.sender,
                cypherText = message.cypherText,
                plainText = decryptedMessage,
                messageKey = messageKey,
                dhPublicKey = message.dhPublicKey,
                chainLength = message.chainLength,
                messageNum = message.messageNum,
            )
        }
    }
    // receive message - incorperate ratchet AND decryption
    //firebase db interactor 117, 167

    //new id constructor for messages on same device
    constructor(oldMessage: Message) : this(
        remoteId = oldMessage.remoteId,
        recipientId = oldMessage.recipientId,
        sender = oldMessage.sender,
        cypherText = oldMessage.cypherText,
        plainText = oldMessage.plainText,
        messageKey = oldMessage.messageKey,
        dhPublicKey = oldMessage.dhPublicKey,
        chainLength = oldMessage.chainLength,
        messageNum = oldMessage.messageNum,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (chainLength != other.chainLength) return false
        if (messageNum != other.messageNum) return false
        if (messageId != other.messageId) return false
        if (remoteId != other.remoteId) return false
        if (recipientId != other.recipientId) return false
        if (sender != other.sender) return false
        if (!cypherText.contentEquals(other.cypherText)) return false
        if (plainText != other.plainText) return false
        if (!dhPublicKey.contentEquals(other.dhPublicKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chainLength
        result = 31 * result + messageNum
        result = 31 * result + messageId.hashCode()
        result = 31 * result + remoteId.hashCode()
        result = 31 * result + recipientId.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + cypherText.contentHashCode()
        result = 31 * result + plainText.hashCode()
        result = 31 * result + dhPublicKey.contentHashCode()
        return result
    }
}

