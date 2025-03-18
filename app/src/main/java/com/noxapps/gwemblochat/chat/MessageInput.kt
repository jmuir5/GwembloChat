package com.noxapps.gwemblochat.chat

import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.noxapps.gwemblochat.R

@Composable
fun MessageInput(){
    var message by remember { mutableStateOf("") }
    var inputColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor =  Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent)
        .padding(4.dp)
    ) {
        TextField(modifier = Modifier
            .weight(1f),
            value = message,
            onValueChange = {
                if (it.length <= 1024) {
                    message = it
                }
            },
            label = {
                Text("Message")
            },
            shape = RoundedCornerShape(20.dp),
            colors = inputColors
        )
        IconButton(
            onClick = {
                //send message
            },

        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_24px),
                contentDescription = "send",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}