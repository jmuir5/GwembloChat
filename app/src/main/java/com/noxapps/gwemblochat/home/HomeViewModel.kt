package com.noxapps.gwemblochat.home

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.data.Relationships
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndLastMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class HomeViewModel(
    navController: NavHostController,
    val auth: FirebaseAuth,
    val db: AppDatabase,
    coroutineScope: CoroutineScope
): ViewModel() {
    fun getAll() = db.chatDao().getAllChatsWithLastMessage(auth.currentUser!!.uid)
    var chatList = listOf<ChatWithUserAndLastMessage>()
    init{
        FirebaseDBInteractor.attachMessageRequestListener(auth, db, coroutineScope)
        FirebaseDBInteractor.attachMessageListener(auth, db, coroutineScope)

    }
    val random = Random(1)
    val chats = (0..10).map{
        /*Chat(
            "chat",


            "testChat$it",
            listOf(
                Message(
                    messageId = UUID.randomUUID(),
                    recipientId = "0",
                    sender = "${it%2}",
                    messageNum = 1,
                    "test message $it, ${(0..random.nextInt(100)).map{"a"}}"
                )
            )
        )*/
    }

}