package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.noxapps.gwemblochat.R

@Composable
fun StyledInput(
    message:MutableState<String>,
    labelText:String? = null,
    leadingIcon:@Composable (() -> Unit)? = null,
    enabled: Boolean,
    singleLine:Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyActSends: Boolean = false,
    onSend:() -> Unit){
    //var message = remember { mutableStateOf("") }
    var inputColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor =  Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent)
        .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(modifier = Modifier
            .weight(1f),
            value = message.value,
            onValueChange = {
                if (it.length <= 1024) {
                    message.value = it
                }
            },
            leadingIcon = leadingIcon,
            label = {
                labelText?.let{Text(it)}
            },
            shape = RoundedCornerShape(20.dp),
            colors = inputColors,
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions =
                if(keyActSends){
                    KeyboardActions{
                        onSend()
                        message.value = ""
                    }
                }
                else{KeyboardActions.Default}

        )
        IconButton(
            onClick = {
                onSend()
                message.value = ""
            },
            enabled = enabled

        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_24px),
                contentDescription = "send",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}

@Composable
fun SearchInput(message:MutableState<String>, enabled:Boolean = true, onSearch:() -> Unit, ){
    StyledInput(
        message = message,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search_24px),
                contentDescription = "search",
            )
        },
        labelText = "Search",
        enabled = enabled,
        onSend = onSearch,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyActSends = true
    )
}