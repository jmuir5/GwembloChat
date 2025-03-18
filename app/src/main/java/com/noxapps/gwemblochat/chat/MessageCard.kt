package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.ui.theme.GwembloChatTheme

@Composable
fun MessageCard(message: Message){
    val sent = message.sender==1
    val cardColor = if(sent){
        MaterialTheme.colorScheme.primary
    } else MaterialTheme.colorScheme.secondaryContainer
    val chatBubbleShape = if(sent){
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    val arrangement = if(sent) Arrangement.End else Arrangement.Start





    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)

    ){
        if (sent){
            Spacer(modifier = Modifier
                .weight(1f)
            )
        }
        Row(modifier = Modifier
            .weight(8f),
            horizontalArrangement = arrangement
        ) {
            Box(
                modifier = Modifier
                    .clip(chatBubbleShape)
                    .background(cardColor)
                    .padding(8.dp),
                ) {
                Text(message.message)
            }
        }
        if (!sent){
            Spacer(modifier = Modifier
                .weight(1f)
            )
        }

    }
}


@Preview
@Composable
fun SentMessageCardPreview(){
    GwembloChatTheme {
        MessageCard(Message(1,"This is a test  sent message message"))
    }
}

@Preview
@Composable
fun ReceivedMessageCardPreview(){
    GwembloChatTheme {
        MessageCard(Message(0,"This is a received  sent message message"))
    }
}

