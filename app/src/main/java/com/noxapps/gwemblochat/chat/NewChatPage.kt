package com.noxapps.gwemblochat.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NewChatPage(
    db: AppDatabase,
    coroutineScope: CoroutineScope,
    auth: FirebaseAuth,
    user:User,
    context: Context
){
    val searchTerm = remember{mutableStateOf("")}
    val firstMessage = remember{mutableStateOf("")}
    var enabled by remember{mutableStateOf(true)}
    var recipient = remember{mutableStateOf<User?>(null)}
    //val noUser by remember{derivedStateOf { recipient.value == User() }}


    Column(modifier = Modifier){
        if(recipient.value == null) {
            SearchInput(
                message = searchTerm,
                enabled = enabled,
                onSearch = {
                    enabled = false
                    FirebaseDBInteractor.getUserByEmail(
                        email = searchTerm.value,
                        onFail = {
                            //onFail
                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                            enabled = true
                        }
                    ) { _, user ->
                        Log.d("NewChatPage", "user found: $user")
                        recipient.value = user
                    }
                }
            )
        }
        else {
            NewChatUserCard(user = recipient)

            Spacer(modifier = Modifier.weight(1f))
            StyledInput(
                message = firstMessage,
                enabled = true,
                labelText = "Message"
            ) {
                val message = auth.currentUser?.let {currentUser ->
                    recipient.value?.let { recipient ->
                        Message(
                            remoteId = currentUser.uid,
                            recipientId = recipient.userId,
                            sender = currentUser.uid,
                            messageNum = 0,
                            message = firstMessage.value
                        )
                    }
                }
                val newChat = recipient.value?.let { recipient ->
                    message?.let { msg ->
                        auth.currentUser?.let {
                            Chat(
                                ownerId = it.uid,
                                partnerId = recipient.userId,
                                lastMessageId = msg.messageId)
                        }
                    }
                }
                coroutineScope.launch {
                    try{recipient.value?.let{db.userDao().insert(it)}}
                    catch (e:Exception){
                        Log.d("NewChatPage", "error inserting user: $e")
                    }
                    message?.let { db.messageDao().insert(it) }
                    newChat?.let { db.chatDao().insert(it) }
                }
                message?.let {
                    FirebaseDBInteractor.upsertMessageRequest(
                        message = it,
                        sender = user
                    )
                }

            }
        }
    }
}