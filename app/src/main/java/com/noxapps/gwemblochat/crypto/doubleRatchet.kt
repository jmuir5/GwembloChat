package com.noxapps.gwemblochat.crypto

import android.util.Log
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndAllMessages
import com.noxapps.gwemblochat.data.toB64String
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.digests.SHA3Digest
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.modes.GCMBlockCipher
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher
import org.bouncycastle.crypto.params.AEADParameters
import org.bouncycastle.crypto.params.HKDFParameters
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import javax.crypto.KeyAgreement


//finally found it: now to reverse engineer
// https://gist.github.com/hurui200320/f86833eaaf0d33574562024f290ae861

object ECDH{
    private const val curveName = "curve25519"
    private val secureRandom = SecureRandom()
    //private var ecSpec = ECNamedCurveTable.getParameterSpec(curveName)
    private val bcProvider = BouncyCastleProvider()
    //A MAX_SKIP constant also needs to be defined. This specifies the maximum number of message keys
    // that can be skipped in a single chain. It should be set high enough to tolerate routine lost or
    // delayed messages, but low enough that a malicious sender can't trigger excessive
    // recipient computation.
    const val MAX_SKIP = 10


    fun generatePrivateKey(): ByteArray =
        X25519PrivateKeyParameters(secureRandom).encoded

    fun generatePublicKey(privateKeyBytes: ByteArray): ByteArray =
        X25519PrivateKeyParameters(privateKeyBytes).generatePublicKey().encoded

    fun doECDH(selfPrivateKeyBytes: ByteArray, remotePublicKeyBytes: ByteArray): ByteArray {
        val agreement = X25519Agreement()
        val result = ByteArray(agreement.agreementSize)
        var localByteArray = selfPrivateKeyBytes.clone()
        var remoteByteArray = remotePublicKeyBytes.clone()
        while(localByteArray.size<32){
            localByteArray += 0
        }
        while(remoteByteArray.size<32){
            remoteByteArray += 0
        }

        if(localByteArray.size>32){} //
        Log.d("selfPrivateKey", localByteArray.toB64String())
        Log.d("remotePublicKey", remoteByteArray.toB64String())

        agreement.init(X25519PrivateKeyParameters(localByteArray))
        agreement.calculateAgreement(X25519PublicKeyParameters(remoteByteArray), result, 0)
        return result
    }

    //Returns a new Diffie-Hellman key pair.
    fun generateKeyPair(): Pair<ByteArray, ByteArray> {
        val privateKeyBytes = generatePrivateKey()
        return privateKeyBytes to generatePublicKey(privateKeyBytes)
    }


    /*private fun dumpKeyPair(keyPair: KeyPair): Pair<BigInteger, ByteArray> {
        val privateKey = keyPair.private
        println("Private key type: " + privateKey.javaClass.canonicalName)
        require(privateKey is BCECPrivateKey)

        val publicKey = keyPair.public
        println("Public key type:  " + publicKey.javaClass.canonicalName)
        require(publicKey is BCECPublicKey)

        // compressed make the pub key shorter, no effect on this demo
        return privateKey.d to publicKey.q.getEncoded(true)
    }

    private fun parsePrivateKey(d: BigInteger): PrivateKey {
        println("Private key (Hex number): " + d.toString(16))
        val privateKeySpec = ECPrivateKeySpec(d, ecSpec)
        val keyFactory = KeyFactory.getInstance("ECDH", bcProvider)
        return keyFactory.generatePrivate(privateKeySpec)
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    private fun parsePublicKey(publicKeyBytes: ByteArray): PublicKey {
        println("Public key (Hex string):  " + publicKeyBytes.toHex())
        val pubKey = ECPublicKeySpec(ecSpec.curve.decodePoint(publicKeyBytes), ecSpec)
        val keyFactory = KeyFactory.getInstance("ECDH", bcProvider)
        return keyFactory.generatePublic(pubKey)
    }

    private fun dumpAndLoadKeyPair(keyPair: KeyPair): Pair<PrivateKey, PublicKey> {
        val (privateKeyD, publicKeyBytes) = dumpKeyPair(keyPair)
        return parsePrivateKey(privateKeyD) to parsePublicKey(publicKeyBytes)
    }*/



    /*fun generateDHPair():KeyPair{
        val keyPairGenerator = KeyPairGenerator.getInstance("ECDH", bcProvider)
        keyPairGenerator.initialize(ECGenParameterSpec(curveName), secureRandom)
        return keyPairGenerator.generateKeyPair()
    }*/

    //Returns the output from the Diffie-Hellman calculation between the private key from the DH key
    // pair dh_pair and the DH public key dh_pub.
    fun diffieHellmanxx(selfPrivateKey: PrivateKey, remotePublicKey: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH", bcProvider)
        keyAgreement.init(selfPrivateKey)
        keyAgreement.doPhase(remotePublicKey, true)
        return keyAgreement.generateSecret()
    }

    // Returns a pair (32-byte root key, 32-byte chain key) as the output of applying a KDF keyed by
    // a 32-byte root key rk to a Diffie-Hellman output dh_out.
    fun kdfRK(
        rootKey:ByteArray,
        dhOutput:ByteArray
    ) :Pair<ByteArray, ByteArray> {
        HKDFBytesGenerator(SHA256Digest()).apply {}
        val hkdf = HKDFBytesGenerator(SHA256Digest())
        val params = HKDFParameters(dhOutput, rootKey, "Gwemblochat".toByteArray())
        var result = ByteArray(32)
        hkdf.init(params)
        hkdf.generateBytes(result, 0, 32)

        return rootKey to result
    }

    //Returns a pair (32-byte chain key, 32-byte message key) as the output of applying a KDF keyed by a 32-byte chain key ck to some constant.
    fun kdfCK(chainKey: ByteArray):Pair<ByteArray, ByteArray> {
        val hmac = HMac(SHA256Digest())
        hmac.init(KeyParameter("Gwemblochat".toByteArray()))
        var result = ByteArray(32)
        hmac.update(chainKey, 0, 32)
        hmac.doFinal(result, 0)
        return chainKey to result
    }

    //Returns an AEAD encryption of plaintext with message key mk [5]. The associated_data is
    // authenticated but is not included in the ciphertext. Because each message key is only used once,
    // the AEAD nonce may handled in several ways: fixed to a constant; derived from mk alongside an
    // independent AEAD encryption key; derived as an additional output from KDF_CK(); or chosen
    // randomly and transmitted.

    //associated data is a byte array (sender identity key||receiver identity key)
    fun encrypt(
        messageKey:ByteArray,
        plaintext: ByteArray,
        associatedData:ByteArray
    ):ByteArray{
        Log.d("messageKey - sending", "messageKey =  ${messageKey.toB64String()}")

        val siv = GCMBlockCipher.newInstance(AESEngine.newInstance())//GCMSIVBlockCipher()
        val digest = SHA3Digest(512)
        val nonce = ByteArray(digest.digestSize)
        digest.update(plaintext, 0, plaintext.size)
        digest.doFinal(nonce, 0)

        siv.init(true, AEADParameters(KeyParameter(messageKey), 128, byteArrayOf(0), associatedData))
        //siv.processBytes(plaintext, 0, plaintext.size, result, 0)
        val result = ByteArray(siv.getOutputSize(plaintext.size))

        val resultSize = siv.processBytes(plaintext, 0, plaintext.size, result, 0)
        siv.doFinal(result, resultSize)
        Log.d("messageSize", "enc - plainText =  ${plaintext.size}")
        Log.d("messageSize", "enc - encrypted =  ${result.size}")
        return result
    }

    //Returns the AEAD decryption of ciphertext with message key mk. If authentication fails, an
    // exception will be raised that terminates processing.
    fun decrypt(
        messageKey:ByteArray,
        ciphertext:ByteArray,
        associatedData:ByteArray
    ):ByteArray{
        Log.d("messageKey - receiving", "messageKey =  ${messageKey.toB64String()}")

        val siv = GCMBlockCipher.newInstance(AESEngine.newInstance())//GCMSIVBlockCipher()
        val digest = SHA3Digest(512)
        val nonce = ByteArray(digest.digestSize)
        digest.update(ciphertext, 0, ciphertext.size)
        digest.doFinal(nonce, 0)

        siv.init(true, AEADParameters(KeyParameter(messageKey), 128, byteArrayOf(0), associatedData))
        //siv.processBytes(ciphertext, 0, ciphertext.size, result, 0)
        val result = ByteArray(siv.getOutputSize(ciphertext.size))

        val resultSize = siv.processBytes(ciphertext, 0, ciphertext.size, result, 0)

        siv.doFinal(result, resultSize)
        Log.d("messageSize", "dec - cipherText =  ${ciphertext.size}")
        Log.d("messageSize", "dec - decrypted =  ${result.size}")
        return result
    }

    //Creates a new message header containing the DH ratchet public key from the key pair in dh_pair,
    // the previous chain length pn, and the message number n. The returned header object contains
    // ratchet public key dh and integers pn and n.
    //literally just the constructor, totally redundant
    fun header(publicKey:ByteArray, chainLength:Int, messageNum:Int):Header{
        return Header(publicKey, chainLength, messageNum)
    }

    //Encodes a message header into a parseable byte sequence, prepends the ad byte sequence,
    // and returns the result. If ad is not guaranteed to be a parseable byte sequence, a length
    // value should be prepended to the output to ensure that the output is parseable as a unique
    // pair (ad, header).
    fun concat(associatedData: ByteArray, header:Header):ByteArray{
        return associatedData + header.dhPublicKey + header.chainLength.toByte() + header.messageNumber.toByte()
    }

    fun ratchetEncrypt(
        chat:Chat,
        plaintext:String,
        associatedDatax:ByteArray,
        db: AppDatabase,
        coroutineScope: CoroutineScope
    ):Pair<Header, ByteArray>{
        val associatedData = byteArrayOf(0)
        val (chainKey, messageKey)  = kdfCK(chat.sentChainKey)
        chat.sentChainKey = chainKey
        Log.d("AD - sending - Header", "dhPublicKey =  ${chat.selfDiffieHellmanPublic.toB64String()}")
        Log.d("AD - sending - Header", "previousChainLength =  ${chat.previousChainLength}")
        Log.d("AD - sending - Header", "messageNum =  ${chat.messagesSent}")
        Log.d("AD - sending - associatedData", "AD =  ${associatedData.toB64String()}")


        val header = header(chat.selfDiffieHellmanPublic, chat.previousChainLength, chat.messagesSent)
        chat.messagesSent += 1
        coroutineScope.launch {
            db.chatDao().update(chat)
        }
        val encrypted = encrypt(messageKey, plaintext.encodeToByteArray(), concat(associatedData, header))
        Log.d("messageSize", "plainText =  ${plaintext.length}")
        Log.d("messageSize", "encrypted =  ${encrypted.size}")

        return Pair(header, encrypted)
    }

    fun ratchetDecrypt(
        chatWUAAM: ChatWithUserAndAllMessages,
        header: Header,
        cypherText:ByteArray,
        associatedDatax:ByteArray,
        db: AppDatabase,
        coroutineScope: CoroutineScope
    ):String{
        val associatedData = byteArrayOf(0)
        Log.d("AD - receiving - Header", "dhPublicKey =  ${header.dhPublicKey.toB64String()}")
        Log.d("AD - receiving - Header", "previousChainLength =  ${header.chainLength}")
        Log.d("AD - receiving - Header", "messageNum =  ${header.messageNumber}")
        Log.d("AD - receiving - associatedData", "AD =  ${associatedData.toB64String()}")

        val plaintext = trySkippedMessages(chatWUAAM, header, cypherText, associatedData, db)
        if(plaintext != null){
            return plaintext
        }
        else{
            if(!header.dhPublicKey.contentEquals(chatWUAAM.chat.partnerDiffieHellmanPublic)) {
                skipMessageKeys(chatWUAAM, header.chainLength, db, coroutineScope)
                dhRatchet(chatWUAAM, header, db, coroutineScope)
            }
            skipMessageKeys(chatWUAAM, header.messageNumber, db, coroutineScope)
            val (chainKey, messageKey) = kdfCK(chatWUAAM.chat.receivedChainKey)
            chatWUAAM.chat.receivedChainKey = chainKey
            Log.d("chainkey", "BANG, ChainKey set to ${messageKey.toB64String()}")

            chatWUAAM.chat.messagesReceived+=1
            coroutineScope.launch {
                db.chatDao().update(chatWUAAM.chat)
            }
            val decrypted = decrypt(messageKey, cypherText, concat(associatedData, header)).copyOfRange(0, cypherText.size-16)
            Log.d("decrypted", "decrypted.toString: ${decrypted}")
            Log.d("decrypted", "decrypted.decodeToString: ${decrypted.decodeToString()}")
            Log.d("decrypted", "decrypted.toB64String: ${decrypted.toB64String()}")
            Log.d("messageSize", "plainText =  ${cypherText.size}")
            Log.d("messageSize", "decrypted =  ${decrypted.size}")
            return decrypted.decodeToString()
        }
    }

    fun trySkippedMessages(
        chat: ChatWithUserAndAllMessages,
        header:Header,
        cypherText: ByteArray,
        associatedData: ByteArray,
        db: AppDatabase
    ):String?{
        for(message in chat.missedMessages){
            if(message.messageNum == header.messageNumber && message.dhPublicKey == header.dhPublicKey){
                //db.referenceDao().delete(message)
                return decrypt(message.messageKey, cypherText, concat(associatedData, header)).toString()
            }
        }
        return null
    }

    fun skipMessageKeys(chat: ChatWithUserAndAllMessages, until:Int, db: AppDatabase, coroutineScope: CoroutineScope) {
        if (chat.chat.messagesReceived + MAX_SKIP < until) {
            throw Error()
        }
        if (chat.chat.receivedChainKey != byteArrayOf()) {
            while (chat.chat.messagesReceived < until) {
                val messageKey = kdfCK(chat.chat.sentChainKey).second
                chat.chat.sentChainKey = messageKey
                Log.d("chainkey", "BANG, ChainKey set to ${messageKey.toB64String()}")
                chat.chat.messagesReceived += 1
                coroutineScope.launch {
                    db.chatDao().update(chat.chat)
                }
            }
        }
    }

    fun dhRatchet(
        chat: ChatWithUserAndAllMessages,
        header: Header,
        db: AppDatabase,
        coroutineScope: CoroutineScope
    ) {
        chat.chat.previousChainLength = chat.chat.messagesReceived
        chat.chat.messagesReceived = 0
        chat.chat.messagesSent = 0
        chat.chat.partnerDiffieHellmanPublic = header.dhPublicKey
        Log.d("keycheck - selfDH", "selfDH: ${chat.chat.selfDiffieHellmanPrivate.toB64String()}")
        Log.d("keycheck - partnerDH", "partnerDH: ${chat.chat.partnerDiffieHellmanPublic.toB64String()}")
        Log.d("keycheck - rootkey", "rootKey: ${chat.chat.rootKey.toB64String()}")
        Log.d("keycheck - chainkey", "chainKey: ${chat.chat.receivedChainKey.toB64String()}")
        var newRootChainKey = kdfRK(
            chat.chat.rootKey,
            doECDH(chat.chat.selfDiffieHellmanPrivate, chat.chat.partnerDiffieHellmanPublic)
        )
        chat.chat.rootKey = newRootChainKey.first
        chat.chat.receivedChainKey = newRootChainKey.second
        Log.d("keycheck - selfDH", "selfDH: ${chat.chat.selfDiffieHellmanPrivate.toB64String()}")
        Log.d("keycheck - partnerDH", "partnerDH: ${chat.chat.partnerDiffieHellmanPublic.toB64String()}")
        Log.d("keycheck - rootkey", "rootKey: ${chat.chat.rootKey.toB64String()}")
        Log.d("keycheck - chainkey", "chainKey: ${chat.chat.receivedChainKey.toB64String()}")
        val neeSelfDHKey = generateKeyPair()
        chat.chat.selfDiffieHellmanPrivate = neeSelfDHKey.first
        chat.chat.selfDiffieHellmanPublic = neeSelfDHKey.second
        newRootChainKey = kdfRK(
            chat.chat.rootKey,
            doECDH(chat.chat.selfDiffieHellmanPrivate, chat.chat.partnerDiffieHellmanPublic)
        )
        chat.chat.rootKey = newRootChainKey.first
        chat.chat.sentChainKey = newRootChainKey.second
        Log.d("chainkey", "BANG, ChainKey set to ${newRootChainKey.second.toB64String()}")
        coroutineScope.launch {
            db.chatDao().update(chat.chat)
        }
    }

}










