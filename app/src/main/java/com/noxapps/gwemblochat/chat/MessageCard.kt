package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noxapps.gwemblochat.data.Message

@Composable
fun MessageCard(message: Message){
    val sent = message.sender==1
    val cardColor = if(sent){
        MaterialTheme.colorScheme.primaryContainer
    } else MaterialTheme.colorScheme.secondaryContainer
    val chatBubbleShape = if(sent){
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)




    Row(modifier = Modifier
        .fillMaxWidth()
        .background(cardColor)
        .clip(chatBubbleShape)
    ){
        if (!sent){
            Spacer(modifier = Modifier
                .weight(1f)
            )
        }
        Box(modifier = Modifier
            .weight(8f)
            .background(cardColor)
            .clip(chatBubbleShape)
        )
        if (sent){
            Spacer(modifier = Modifier
                .weight(1f)
            )
        }

    }
}

