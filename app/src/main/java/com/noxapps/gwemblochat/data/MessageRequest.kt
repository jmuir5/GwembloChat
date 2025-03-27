package com.noxapps.gwemblochat.data

data class MessageRequest(
    val message:Message = Message(),
    val user:User = User()
) {
}