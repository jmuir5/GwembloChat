package com.noxapps.gwemblochat.home

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import kotlinx.coroutines.CoroutineScope
import java.util.UUID
import kotlin.random.Random

class HomeViewModel(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: AppDatabase,
    coroutineScope: CoroutineScope
): ViewModel() {
    init{
        FirebaseDBInteractor.attachMessageListener(auth, db, coroutineScope)
    }
    val random = Random(1)
    val chats = (0..10).map{
        Chat(
            "testChat$it",
            listOf(
                Message(
                    messageId = UUID.randomUUID(),
                    recipientId = 0,
                    sender = it%2,
                    messageNum = 1,
                    "test message $it, ${(0..random.nextInt(100)).map{"a"}}"
                )
            )
        )
    }

}