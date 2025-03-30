package com.noxapps.gwemblochat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.gwemblochat.auth.LoginPage
import com.noxapps.gwemblochat.chat.ChatPage
import com.noxapps.gwemblochat.chat.NewChatPage
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.SampleData
import com.noxapps.gwemblochat.home.HomePage

@Composable
fun NavMain(){
    val navHostController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "gift-app-database"
    ).fallbackToDestructiveMigration().build()
    val auth = Firebase.auth

    val currentUser = remember { mutableStateOf( SampleData.nullUser ) }
    val startPoint = if(auth.currentUser == null) Paths.Login.Path else Paths.Home.Path



    NavHost(navController = navHostController, startDestination = startPoint) {
        composable(Paths.Home.Path) {
            HomePage(
                navController = navHostController,
                auth = auth,
                db = db,
                coroutineScope = coroutineScope,
            )
        }
        composable(
            route = "${Paths.Chat.Path}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.IntType }
            )
        ) {
            val chatId = it.arguments?.getInt("chatId")
            chatId?.let{
                ChatPage(
                    chatId=it,
                    auth = auth,
                    coroutineScope = coroutineScope,
                    db = db,
                )
            }

        }
        composable(Paths.Login.Path) {
            LoginPage(
                auth = auth,
                user = currentUser,
                db = db,
                navHostController = navHostController
            )
        }
        composable(Paths.NewChat.Path) {
            NewChatPage(
                navHostController,
                db,
                coroutineScope,
                auth,
                currentUser.value,
                context
            )
        }
    }
}