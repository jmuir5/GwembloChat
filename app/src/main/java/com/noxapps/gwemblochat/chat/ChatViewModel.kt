package com.noxapps.gwemblochat.chat

import androidx.lifecycle.ViewModel
import com.noxapps.gwemblochat.data.Message

class ChatViewModel(): ViewModel() {
    val messages = (1..10).map{ Message(it%2, "test message $it") }
}