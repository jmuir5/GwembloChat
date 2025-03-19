package com.noxapps.gwemblochat.home

import androidx.lifecycle.ViewModel
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.Message
import kotlin.random.Random

class HomeViewModel(): ViewModel() {
    val random = Random(1)
    val chats = (0..10).map{
        Chat(
            "testChat$it",
            listOf(
                Message(
                    it%2,
                    "test message $it, ${(0..random.nextInt(15)).map{"a"}}"
                )
            )
        )
    }

}