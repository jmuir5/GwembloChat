package com.noxapps.gwemblochat.crypto

import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndAllMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bouncycastle.asn1.sec.SECNamedCurves
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey


//finally found it: now to reverse engineer
// https://gist.github.com/hurui200320/f86833eaaf0d33574562024f290ae861

//Returns a new Diffie-Hellman key pair.
fun generateDHPair():KeyPair{
    val kpg = KeyPairGenerator.getInstance("DiffieHellman")
    kpg.initialize(2048)

    return kpg.generateKeyPair()

}

//Returns the output from the Diffie-Hellman calculation between the private key from the DH key
// pair dh_pair and the DH public key dh_pub. If the DH function rejects invalid public keys,
// then this function may raise an exception which terminates processing.
fun diffieHellman(
    dhPair:KeyPair,
    dhPub: PublicKey
):String{
    return ""
}

// Returns a pair (32-byte root key, 32-byte chain key) as the output of applying a KDF keyed by
// a 32-byte root key rk to a Diffie-Hellman output dh_out.
fun kdfRK(rootKey:String, dhOutput:String
) :String {
    return ""
}

//Returns a pair (32-byte chain key, 32-byte message key) as the output of applying a KDF keyed by a 32-byte chain key ck to some constant.
fun kdfCK(chainKey:String):String {
    return ""
}

//Returns an AEAD encryption of plaintext with message key mk [5]. The associated_data is
// authenticated but is not included in the ciphertext. Because each message key is only used once,
// the AEAD nonce may handled in several ways: fixed to a constant; derived from mk alongside an
// independent AEAD encryption key; derived as an additional output from KDF_CK(); or chosen
// randomly and transmitted.
fun encrypt(messageKey:String, plaintext:String, associatedData:String):String{
    return ""
}

//Returns the AEAD decryption of ciphertext with message key mk. If authentication fails, an
// exception will be raised that terminates processing.
fun decrypt(messageKey:String, ciphertext:String, associatedData:String):String{
    return ""
}

//Creates a new message header containing the DH ratchet public key from the key pair in dh_pair,
// the previous chain length pn, and the message number n. The returned header object contains
// ratchet public key dh and integers pn and n.
fun header(dhPair:KeyPair, chainLength:Int, messageNum:Int):Header{
    return Header(dhPair.public/*public key*/, chainLength, messageNum)
}
//Encodes a message header into a parseable byte sequence, prepends the ad byte sequence,
// and returns the result. If ad is not guaranteed to be a parseable byte sequence, a length
// value should be prepended to the output to ensure that the output is parseable as a unique
// pair (ad, header).
fun concat(associatedData:String, header:Header):String{
    return ""
}

//A MAX_SKIP constant also needs to be defined. This specifies the maximum number of message keys
// that can be skipped in a single chain. It should be set high enough to tolerate routine lost or
// delayed messages, but low enough that a malicious sender can't trigger excessive
// recipient computation.
const val MAX_SKIP = 10

fun ratchetEncrypt(
    chat:Chat,
    plaintext:String,
    associatedData:String,
    db: AppDatabase,
    coroutineScope: CoroutineScope
):Pair<Header, String>{
    val messageKey = kdfCK(chat.sentChainKey)
    chat.sentChainKey = messageKey
    val header = header(chat.selfDiffieHellmanKey, chat.previousChainLength, chat.messagesSent)
    chat.messagesSent += 1
    coroutineScope.launch {
        db.chatDao().update(chat)
    }
    return Pair(header, encrypt(messageKey, plaintext, concat(associatedData, header)))
}

fun ratchetDecrypt(
    chat: ChatWithUserAndAllMessages,
    header: Header,
    cypherText:String,
    associatedData:String,
    db: AppDatabase,
    coroutineScope: CoroutineScope
):String{
    val plaintext = trySkippedMessages(chat, header, cypherText, associatedData, db)
    if(plaintext != null){
        return plaintext
    }
    else{
        if(header.dhPublicKey != chat.chat.partnerDiffieHellmanKey) {
            skipMessageKeys(chat, header.chainLength, db, coroutineScope)
            dhRatchet(chat, header, db, coroutineScope)
        }
        skipMessageKeys(chat, header.messageNumber, db, coroutineScope)
        val messageKey = kdfCK(chat.chat.sentChainKey)
        chat.chat.sentChainKey = messageKey
        chat.chat.messagesReceived+=1
        coroutineScope.launch {
            db.chatDao().update(chat.chat)
        }
        return decrypt(messageKey, cypherText, concat(associatedData, header))
    }
}

fun trySkippedMessages(
    chat: ChatWithUserAndAllMessages,
    header:Header,
    cypherText: String,
    associatedData: String,
    db: AppDatabase
):String?{
    for(message in chat.missedMessages){
        if(message.messageNum == header.messageNumber && message.dhPublicKey == header.dhPublicKey){
            //db.referenceDao().delete(message)
            return decrypt(message.plainText, cypherText, concat(associatedData, header))
        }
    }
    return null
}

fun skipMessageKeys(chat: ChatWithUserAndAllMessages, until:Int, db: AppDatabase, coroutineScope: CoroutineScope) {
    if (chat.chat.messagesReceived + MAX_SKIP < until) {
        throw Error()
    }
    if (chat.chat.receivedChainKey != "") {
        while (chat.chat.messagesReceived < until) {
            val messageKey = kdfCK(chat.chat.sentChainKey)
            chat.chat.sentChainKey = messageKey
            chat.chat.messagesReceived += 1
            coroutineScope.launch {
                db.chatDao().update(chat.chat)
            }
        }
    }
}

fun dhRatchet(chat: ChatWithUserAndAllMessages, header: Header, db: AppDatabase, coroutineScope: CoroutineScope) {
    chat.chat.previousChainLength = chat.chat.messagesReceived
    chat.chat.messagesReceived = 0
    chat.chat.messagesSent = 0
    chat.chat.partnerDiffieHellmanKey = header.dhPublicKey
    val newKeyA = kdfRK(
        chat.chat.rootKey,
        diffieHellman(chat.chat.selfDiffieHellmanKey, chat.chat.partnerDiffieHellmanKey)
    )
    chat.chat.receivedChainKey = newKeyA
    chat.chat.rootKey = newKeyA
    chat.chat.selfDiffieHellmanKey = generateDHPair()
    val newKeyB = kdfRK(
        chat.chat.rootKey,
        diffieHellman(chat.chat.selfDiffieHellmanKey, chat.chat.partnerDiffieHellmanKey)
    )
    chat.chat.sentChainKey = newKeyB
    chat.chat.rootKey = newKeyB
    coroutineScope.launch {
        db.chatDao().update(chat.chat)
    }
}


