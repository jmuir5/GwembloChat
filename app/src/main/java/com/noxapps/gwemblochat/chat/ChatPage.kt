package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.ui.theme.GwembloChatTheme
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChatPage(
    auth: FirebaseAuth,
    coroutineScope: CoroutineScope,
    viewModel: ChatViewModel = ChatViewModel()
){


    val message = remember{ mutableStateOf("")}
    LaunchedEffect(coroutineScope) {
        //listOfGifts = db.userDao().getOneWithGiftsAndListsById(user.userId).giftsWithLists//.giftDao().getGiftsWithLists()
    }

    Scaffold(
        topBar = {
            ChatHeader(viewModel.chatTarget, viewModel.chatTargetProfilePic)
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .weight(1f)
            ) {
                viewModel.messages.forEach{
                    item {
                        auth.currentUser?.let { it1 -> MessageCard(it, it1.uid) }
                    }
                }
            }
            StyledInput(
                message = message,
                enabled = true,
                onSend = {
                    val messageObject = auth.currentUser?.let {
                        Message(
                            recipientId = "recipient.value.userId",
                            sender = it.uid,
                            messageNum = 0,
                            message = message.value
                        )
                    }

                    messageObject?.let { FirebaseDBInteractor.upsertMessage(it) }
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
