package com.noxapps.gwemblochat.chat

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(userName: String){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val headerBarColors = TopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Surface(shadowElevation = 8.dp) {
        TopAppBar(
            title = {
                Text(
                    userName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {

            },
            actions = {

            },
            modifier = Modifier,
            scrollBehavior = scrollBehavior,
            colors = headerBarColors
        )
    }
}