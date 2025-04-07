package com.noxapps.gwemblochat.crypto

import java.security.PublicKey

data class Header(
    val dhPublicKey: PublicKey,
    val chainLength: Int,
    val messageNumber: Int
) {
}