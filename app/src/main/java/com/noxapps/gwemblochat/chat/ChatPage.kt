package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import com.noxapps.gwemblochat.Greeting
import com.noxapps.gwemblochat.ui.theme.GwembloChatTheme

@Composable
fun ChatPage(
    viewModel: ChatViewModel = ChatViewModel()
){
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
                        MessageCard(it)
                    }
                }
            }
            MessageInput()
        }

    }
}


@Preview(showBackground = true)
@Composable
fun ChatPagePreview(){
    GwembloChatTheme {
        ChatPage()
    }
}
