package com.noxapps.gwemblochat.home

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.Message
import kotlin.random.Random

class HomeViewModel(
    navController: NavHostController,
    auth: FirebaseAuth
): ViewModel() {
    val random = Random(1)
    val chats = (0..10).map{
        Chat(
            "testChat$it",
            listOf(
                Message(
                    messageId = it,
                    conversationId = 0,
                    sender = it%2,
                    "test message $it, ${(0..random.nextInt(100)).map{"a"}}"
                )
            )
        )
    }

}