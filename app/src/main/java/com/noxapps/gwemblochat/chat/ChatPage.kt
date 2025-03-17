package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
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

    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        ChatHeader()
        LazyColumn(modifier = Modifier
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


@Preview(showBackground = true)
@Composable
fun ChatPagePreview(){
    GwembloChatTheme {
        ChatPage()
    }
}
