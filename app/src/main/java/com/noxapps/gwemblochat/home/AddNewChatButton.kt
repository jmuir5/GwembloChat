package com.noxapps.gwemblochat.home

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AddNewChatButton(navController: NavHostController){
    FloatingActionButton(
        onClick = { navController.navigate("NewChat") },
        containerColor = MaterialTheme.colorScheme.primary
    ){
        Icon(
            androidx.compose.material.icons.Icons.Filled.Add,
            contentDescription = "Add New Chat"
        )
    }
}