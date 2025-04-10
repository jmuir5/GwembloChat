package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noxapps.gwemblochat.crypto.ECDH
import java.security.KeyPair

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: String = "",
    val partnerId: String = "",
    val lastMessageId: String = "",
    //val activated : Boolean=true,
    //double ratchet data
    var selfDiffieHellmanPrivate: ByteArray = byteArrayOf(),
    var selfDiffieHellmanPublic: ByteArray = byteArrayOf(),
    //var partnerDiffieHellmanPrivate: ByteArray = byteArrayOf(),
    var partnerDiffieHellmanPublic: ByteArray = byteArrayOf(),
    //val partnerIdentiryPublicKey


    var rootKey: ByteArray = byteArrayOf(),
    var sentChainKey: ByteArray = byteArrayOf(),
    var receivedChainKey: ByteArray = byteArrayOf(),
    var messagesSent: Int = 0,
    var messagesReceived: Int = 0,
    var previousChainLength: Int = 0,

    //MKSKIPPED: Dictionary of skipped-over message keys, indexed by ratchet public
    // key and message number. Raises an exception if too many elements are stored.

) {
    //minimum new chat constructor
    companion object{
        fun initNewChat(
            ownerId: String,
            partnerId: String,
            partnerDHPublicKey: ByteArray,
            secretKey: ByteArray,
        ):Chat{
            val keyPair = ECDH.generateKeyPair()
            return Chat(
                ownerId = ownerId,
                partnerId = partnerId,
                selfDiffieHellmanPrivate = keyPair.first,
                selfDiffieHellmanPublic = keyPair.second,
                partnerDiffieHellmanPublic = partnerDHPublicKey,
                rootKey = ECDH.kdfRK(secretKey, ECDH.doECDH(keyPair.first, partnerDHPublicKey)).first,
                sentChainKey = ECDH.kdfRK(secretKey, ECDH.doECDH(keyPair.first, partnerDHPublicKey)).second,
                //state.RK, state.CKs = KDF_RK(SK, DH(state.DHs, state.DHr))
            )
        }
    }



    //update last received message constructor
    constructor(chat:Chat, messageId: String) : this(
        id = chat.id,
        ownerId = chat.ownerId,
        partnerId = chat.partnerId,
        lastMessageId = messageId

    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (id != other.id) return false
        if (messagesSent != other.messagesSent) return false
        if (messagesReceived != other.messagesReceived) return false
        if (previousChainLength != other.previousChainLength) return false
        if (ownerId != other.ownerId) return false
        if (partnerId != other.partnerId) return false
        if (lastMessageId != other.lastMessageId) return false
        if (!selfDiffieHellmanPrivate.contentEquals(other.selfDiffieHellmanPrivate)) return false
        if (!selfDiffieHellmanPublic.contentEquals(other.selfDiffieHellmanPublic)) return false
        if (!partnerDiffieHellmanPublic.contentEquals(other.partnerDiffieHellmanPublic)) return false
        if (!rootKey.contentEquals(other.rootKey)) return false
        if (!sentChainKey.contentEquals(other.sentChainKey)) return false
        if (!receivedChainKey.contentEquals(other.receivedChainKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + messagesSent
        result = 31 * result + messagesReceived
        result = 31 * result + previousChainLength
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + partnerId.hashCode()
        result = 31 * result + lastMessageId.hashCode()
        result = 31 * result + selfDiffieHellmanPrivate.contentHashCode()
        result = 31 * result + selfDiffieHellmanPublic.contentHashCode()
        result = 31 * result + partnerDiffieHellmanPublic.contentHashCode()
        result = 31 * result + rootKey.contentHashCode()
        result = 31 * result + sentChainKey.contentHashCode()
        result = 31 * result + receivedChainKey.contentHashCode()
        return result
    }

}