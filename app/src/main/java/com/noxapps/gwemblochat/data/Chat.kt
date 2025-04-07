package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noxapps.gwemblochat.crypto.diffieHellman
import com.noxapps.gwemblochat.crypto.generateDHPair
import com.noxapps.gwemblochat.crypto.kdfRK
import java.security.KeyPair
import javax.crypto.SecretKey

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: String = "",
    val partnerId: String = "",
    val lastMessageId: String = "",
    val activated : Boolean=true,
    //double ratchet data
    var selfDiffieHellmanKey: KeyPair = generateDHPair(),
    var partnerDiffieHellmanKey: KeyPair = generateDHPair(),
    var rootKey: String = "",//kdfRK(),
    var sentChainKey: String = "",
    var receivedChainKey: String = "",
    var messagesSent: Int = 0,
    var messagesReceived: Int = 0,
    var previousChainLength: Int = 0,

    //MKSKIPPED: Dictionary of skipped-over message keys, indexed by ratchet public
    // key and message number. Raises an exception if too many elements are stored.

) {
    constructor(
        ownerId: String,
        partnerId: String,
        selfDHKeyPair: KeyPair = generateDHPair(),
        partnerDHPublicKey: KeyPair,
        secretKey: SecretKey
    ) : this(
        ownerId = ownerId,
        partnerId = partnerId,
        selfDiffieHellmanKey = selfDHKeyPair,
        partnerDiffieHellmanKey = partnerDHPublicKey,
        rootKey = kdfRK(secretKey.encoded.toString(), diffieHellman(selfDHKeyPair, partnerDHPublicKey)),
        sentChainKey = kdfRK(secretKey.encoded.toString(), diffieHellman(selfDHKeyPair, partnerDHPublicKey)),
        receivedChainKey = kdfRK(secretKey.encoded.toString(), diffieHellman(selfDHKeyPair, partnerDHPublicKey)),
    )
    constructor(chat:Chat, messageId: String) : this(
        id = chat.id,
        ownerId = chat.ownerId,
        partnerId = chat.partnerId,
        lastMessageId = messageId

    )
}