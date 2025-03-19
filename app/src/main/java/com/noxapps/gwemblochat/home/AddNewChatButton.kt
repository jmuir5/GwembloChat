package com.noxapps.gwemblochat.home

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AddNewChatButton(){
    FloatingActionButton(
        onClick = { /*TODO*/ },
        containerColor = MaterialTheme.colorScheme.primary
    ){
        Icon(
            androidx.compose.material.icons.Icons.Filled.Add,
            contentDescription = "Add New Chat"
        )
    }
}