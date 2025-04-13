package com.noxapps.gwemblochat.chat

import android.util.Log
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
import com.noxapps.gwemblochat.crypto.ECDH
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.data.Relationships
import com.noxapps.gwemblochat.data.User
import com.noxapps.gwemblochat.data.toB64String
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
    var thisUser by remember { mutableStateOf(User()) }
    val messages = viewModel.getMessages(chatObject.user.userId).collectAsState(initial = null)

    val message = remember{ mutableStateOf("")}
    LaunchedEffect(coroutineScope) {
        chatObject = db.chatDao().getChatWithUserById(chatId)
        thisUser = db.userDao().getOneById(auth.currentUser!!.uid)
        Log.d("chainkey", "opened chatPage. chainkey = ${chatObject.chat.sentChainKey.toB64String()}")
        //listOfGifts = db.userDao().getOneWithGiftsAndListsById(user.userId).giftsWithLists//.giftDao().getGiftsWithLists()
    }

    Scaffold(
        topBar = {
            ChatHeader(chatObject.user.userName)
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
                    val encryptedMessage = ECDH.ratchetEncrypt(
                        chatObject.chat,
                        message.value,
                        ECDH.doECDH(thisUser.identityPrivateKey, chatObject.user.identityPublicKey),
                        db,
                        coroutineScope
                    )

                    coroutineScope.launch {
                        Log.d("chainkey", "opened chatPage. chainkey = ${chatObject.chat.sentChainKey.toB64String()}")
                        db.chatDao().upsert(chatObject.chat)
                    }
                    val messageObject = auth.currentUser?.let {
                        Message(
                            remoteId = chatObject.user.userId,
                            recipientId = chatObject.user.userId,
                            sender = it.uid,
                            _cypherText = encryptedMessage.second.toB64String(),
                            messageNum = chatObject.chat.messagesSent,
                            plainText = message.value,
                            _dhPublicKey = chatObject.chat.selfDiffieHellmanPublic.toB64String(),
                            chainLength = chatObject.chat.previousChainLength,
                        )
                    }
                    val sentMessage = auth.currentUser?.let {currentUser ->
                        Message(
                            remoteId = currentUser.uid,
                            recipientId = chatObject.chat.partnerId,
                            sender = currentUser.uid,
                            _cypherText = encryptedMessage.second.toB64String(),
                            messageNum = chatObject.chat.messagesSent-1,
                            _dhPublicKey = chatObject.chat.selfDiffieHellmanPublic.toB64String(),
                            chainLength = chatObject.chat.previousChainLength
                        )
                    }
                    sentMessage?.let { FirebaseDBInteractor.upsertMessage(it) }

                    messageObject?.let {
                        coroutineScope.launch {
                            db.messageDao().insert(it)
                            Log.d("chainkey", "opened chatPage. chainkey = ${chatObject.chat.sentChainKey.toB64String()}")
                            chatObject.chat.lastMessageId = it.messageId
                            db.chatDao().update(chatObject.chat)
                        }

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
