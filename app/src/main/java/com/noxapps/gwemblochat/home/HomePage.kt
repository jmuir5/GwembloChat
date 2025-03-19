package com.noxapps.gwemblochat.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.noxapps.gwemblochat.chat.ChatHeader
import com.noxapps.gwemblochat.chat.ChatViewModel
import com.noxapps.gwemblochat.chat.MessageCard
import com.noxapps.gwemblochat.chat.MessageInput

@Composable
fun HomePage(
    viewModel: HomeViewModel = HomeViewModel()
){
    Scaffold(
        topBar = {
            HomeHeader()
        },
        floatingActionButton = {
            AddNewChatButton()
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
        ) {
            item{
                ClickableSearchBar()
            }
            viewModel.chats.forEach{
                item {
                    ChatCard(it)
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview(){
    HomePage()
}