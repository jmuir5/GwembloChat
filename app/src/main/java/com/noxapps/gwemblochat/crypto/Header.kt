package com.noxapps.gwemblochat.crypto

import java.security.PublicKey

class Header(
    val dhPublicKey: ByteArray,
    val chainLength: Int,
    val messageNumber: Int
) {
}