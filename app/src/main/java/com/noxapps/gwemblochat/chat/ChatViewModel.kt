package com.noxapps.gwemblochat.chat

import androidx.lifecycle.ViewModel
import com.noxapps.gwemblochat.data.Message
import kotlin.random.Random

class ChatViewModel(): ViewModel() {
    val random = Random(1)
    val messages = (1..10).map{
        Message(
            messageId = it,
            conversationId = 0,
            sender = it%2,
            "test message $it, ${(0..random.nextInt(100)).map{"a"}}"
        )
    }
    val chatTarget = "Example Chat"
    val chatTargetProfilePic = ""
}