package com.noxapps.gwemblochat.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndLastMessage
import kotlinx.coroutines.CoroutineScope

@Composable
fun HomePage(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: AppDatabase,
    coroutineScope: CoroutineScope,
    viewModel: HomeViewModel = HomeViewModel(
        navController,
        auth,
        db,
        coroutineScope
    ),

){
    var chatList = viewModel.getAll().collectAsState(initial = emptyList())
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
            AddNewChatButton(navController)
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
        ) {
            item{
                ClickableSearchBar()
            }
            chatList.value.forEach{
                item {
                    ChatCard(it, navController)
                }
            }
        }

    }
}


/*
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
*/