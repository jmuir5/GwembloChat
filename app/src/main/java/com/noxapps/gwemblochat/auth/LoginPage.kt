package com.noxapps.familygiftlist.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.navigation.loggedCheck
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun LoginPage(
    auth: FirebaseAuth,
    user: MutableState<User>,
    db: AppDatabase,
    navHostController: NavHostController,
    viewModel: LoginViewModel = LoginViewModel(
        auth,
        db,
        navHostController
    )
){
    loggedCheck(navHostController, auth, user.value )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var status = remember {mutableStateOf(true)}     //login(true) / register(false) status
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
    val textIconColor = MaterialTheme.colorScheme.onPrimaryContainer

    val scrollState = rememberScrollState()
    var backDoubleTap by remember {mutableStateOf(false)}
    BackHandler(){
        if(backDoubleTap) {
            (context as Activity).finish()
            exitProcess(0)
        }
        else{
            backDoubleTap = true
            Toast.makeText(
                context,
                "Doubletap back to exit",
                Toast.LENGTH_SHORT,
            ).show()
            coroutineScope.launch{
                Thread.sleep(1000)
                MainScope().launch{
                    backDoubleTap = false
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(state = scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()


            ) {
                LogoCard()
                if (status.value)
                    LoginCard(textFieldColors, textIconColor, status, user, viewModel)
                else
                    RegisterCard(textFieldColors, textIconColor, status, user, viewModel)

            }
        }
    }

}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    FamilyGiftListTheme {
        val textFieldColors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        val textIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        val state = remember{mutableStateOf(true)}
        val auth = Firebase.auth
        val navController = rememberNavController()
        val db = Room.databaseBuilder(
            LocalContext.current,
            AppDatabase::class.java, "gift-app-test-database"
        ).build()
        val user = remember{mutableStateOf<User>(sampleData.nullUser)}


        LoginCard(textFieldColors, textIconColor, state,user, LoginViewModel(auth, db, navController))
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    FamilyGiftListTheme {
        val textFieldColors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        val textIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        val state = remember{mutableStateOf(true)}
        val auth = Firebase.auth
        val navController = rememberNavController()
        val db = Room.databaseBuilder(
            LocalContext.current,
            AppDatabase::class.java, "gift-app-test-database"
        ).build()
        val user = remember{mutableStateOf<User>(sampleData.nullUser)}


        RegisterCard(textFieldColors, textIconColor, state, user, LoginViewModel(auth, db, navController))
    }
}

@Preview(showBackground = true)
@Composable
fun LogoPreview() {
    FamilyGiftListTheme {

        LogoCard()
    }
}