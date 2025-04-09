package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.noxapps.gwemblochat.crypto.ECDH

@Entity
class User(
    @PrimaryKey val userId:String="",
    val email: String = "",
    var userName: String = "",
    @Exclude val identityPrivateKey: ByteArray = byteArrayOf(),
    val identityPublicKey: ByteArray = byteArrayOf(),
    ) {
    companion object{
        fun init(
            userId: String,
            userName: String,
            email: String
        ):User{
            val keyPair = ECDH.generateKeyPair()
            return User(
                userId = userId,
                userName = userName,
                email = email,
                identityPrivateKey = keyPair.first,
                identityPublicKey = keyPair.second
            )
        }
    }
}