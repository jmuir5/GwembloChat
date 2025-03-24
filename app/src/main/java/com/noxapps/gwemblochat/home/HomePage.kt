package com.noxapps.gwemblochat.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.chat.ChatHeader
import com.noxapps.gwemblochat.chat.ChatViewModel
import com.noxapps.gwemblochat.chat.MessageCard
import com.noxapps.gwemblochat.chat.MessageInput
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.Message
import kotlin.random.Random

@Composable
fun HomePage(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: HomeViewModel = HomeViewModel(navController, auth),

){
    Scaffold(
        topBar = {
            HomeHeader(text = "GwembloChat"){
                DropdownMenuItem(
                    text = { Text("Sign out") },
                    onClick = {
                        auth.signOut()
                        navController.navigate("login"){
                            popUpToRoute
                        }
                    }
                )
            }
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
                    ChatCard(it, navController)
                }
            }
        }

    }
}



@Preview(showBackground = true)
@Composable
fun HomePagePreview(){
    val random = Random(1)
    val chats = (0..10).map{
        Chat(
            "testChat$it",
            listOf(
                Message(
                    messageId = it,
                    conversationId = 0,
                    sender = it%2,
                    "test message $it, ${(0..random.nextInt(100)).map{"a"}}"
                )
            )
        )
    }
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            HomeHeader(text = "GwembloChat")
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
            chats.forEach{
                item {
                    ChatCard(it, navController)
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun HomeHeaderPreview(){
    HomeHeader(text = "GwembloChat")
}

@Preview(showBackground = true)
@Composable
fun ChatCardPreview(){
    val chat = Chat(
        "testChat",
        listOf(
            Message(
                messageId = 0,
                conversationId = 0,
                sender = 0,
                "test message"
            )
        )
    )
    val navController = rememberNavController()
    ChatCard(chat, navController)
}
