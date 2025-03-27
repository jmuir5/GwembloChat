package com.noxapps.gwemblochat.chat

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import com.noxapps.gwemblochat.data.Message
import kotlinx.coroutines.CoroutineScope
import java.util.UUID
import kotlin.random.Random

class ChatViewModel(
): ViewModel() {
    init{}
    val random = Random(1)
    val messages = (1..10).map{
        Message(
            messageId = UUID.randomUUID().toString(),
            remoteId = "0",
            recipientId = "0",
            sender = "${it%2}",
            messageNum = it,
            "test message $it, ${(0..random.nextInt(100)).map{"a"}}"
        )
    }
    val chatTarget = "Example Chat"
    val chatTargetProfilePic = ""
}