package com.noxapps.gwemblochat.chat

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.data.Relationships
import com.noxapps.gwemblochat.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class ChatViewModel(
    val chatId:Int,
    val auth: FirebaseAuth,
    coroutineScope: CoroutineScope,
    val db: AppDatabase,
): ViewModel() {
    fun getAll() = db.chatDao().getAllChatsWithLastMessage(auth.currentUser!!.uid)
    fun populate() = db.chatDao().getChatByIdWithAllMessages(chatId)
    fun getChatInfo() = db.chatDao().getChatByIdWithAllMessages(chatId)
    var chat = Relationships.ChatWithUser(Chat(), User())
    fun getMessages(id:String) = db.messageDao().getAllMessagesByRemoteId(id)

    init{
        coroutineScope.launch {
            val holder = db.chatDao().getChatWithUserById(chatId)
            MainScope().launch {
                chat = holder
            }

        }
    }
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