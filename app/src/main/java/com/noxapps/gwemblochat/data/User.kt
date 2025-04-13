package com.noxapps.gwemblochat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.noxapps.gwemblochat.crypto.ECDH
import java.util.Base64

@Entity
class User(
    @PrimaryKey val userId:String="",
    val email: String = "",
    var userName: String = "",
    @Exclude @set:Exclude @get:Exclude var _identityPrivateKey: String = byteArrayOf().toB64String(),
    var _identityPublicKey: String = byteArrayOf().toB64String(),
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
                _identityPrivateKey = keyPair.first.toB64String(),
                _identityPublicKey = keyPair.second.toB64String()
            )
        }
    }

    var identityPrivateKey:ByteArray
        @Exclude get() {return Base64.getDecoder().decode(_identityPublicKey)}
        @Exclude set(value) {
            _identityPrivateKey = Base64.getEncoder().encodeToString(value)
        }

    var identityPublicKey:ByteArray
        @Exclude get() {return Base64.getDecoder().decode(_identityPublicKey)}
        @Exclude set(value) {
            _identityPublicKey = Base64.getEncoder().encodeToString(value)
        }


}


