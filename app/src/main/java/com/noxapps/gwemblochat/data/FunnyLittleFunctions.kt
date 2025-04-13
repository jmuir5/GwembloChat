package com.noxapps.gwemblochat.data

import java.util.Base64

fun ByteArray.toB64String(): String {
    return Base64.getEncoder().encodeToString(this)
}