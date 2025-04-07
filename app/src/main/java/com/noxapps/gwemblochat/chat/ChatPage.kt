package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.data.Relationships
import com.noxapps.gwemblochat.data.User
import com.noxapps.gwemblochat.ui.theme.GwembloChatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChatPage(
    chatId:Int,
    auth: FirebaseAuth,
    coroutineScope: CoroutineScope,
    db: AppDatabase,
    viewModel: ChatViewModel = ChatViewModel(chatId, auth, coroutineScope, db)
){
    //var chatList = viewModel.getAll().collectAsState(initial = emptyList())
    var chatObject by remember { mutableStateOf(Relationships.ChatWithUser(Chat(), User())) }
    val messages = viewModel.getMessages(chatObject.user.userId).collectAsState(initial = null)

    val message = remember{ mutableStateOf("")}
    LaunchedEffect(coroutineScope) {
        chatObject = db.chatDao().getChatWithUserById(chatId)
        //listOfGifts = db.userDao().getOneWithGiftsAndListsById(user.userId).giftsWithLists//.giftDao().getGiftsWithLists()
    }

    Scaffold(
        topBar = {
            ChatHeader(chatObject.user.userName, chatObject.user.profilePic)
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .weight(1f),
                reverseLayout = true
            ) {
                messages.value?.reversed().let{msgs->
                    msgs?.forEach{
                        item {
                            auth.currentUser?.let { user -> MessageCard(it, user.uid) }
                        }
                    }
                }
            }
            StyledInput(
                message = message,
                enabled = true,
                onSend = {
                    val messageObject = auth.currentUser?.let {
                        Message(
                            remoteId = chatObject.user.userId,
                            recipientId = chatObject.user.userId,
                            sender = it.uid,
                            messageNum = messages.value?.size?: 0 ,
                            plainText = message.value
                        )
                    }

                    messageObject?.let {
                        coroutineScope.launch {
                            db.messageDao().insert(it)
                            db.chatDao().update(Chat(chatObject.chat, it.messageId))
                        }
                        FirebaseDBInteractor.upsertMessage(it)
                    }
                    message.value = ""
                }
            )
        }

    }
}


@Preview(showBackground = true, heightDp = 700)
@Composable
fun ChatPagePreview(){
    GwembloChatTheme {
        //ChatPage()
    }
}
