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
import java.util.UUID

@Composable
fun MessageCard(message: Message, myId:String){
    val sent = message.sender==myId
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
        val sameId = "same"
        MessageCard(
            Message(
                messageId = UUID.randomUUID(),
                recipientId = "different",
                sender = sameId,
                messageNum = 1,
                "test message"
            ), sameId
        )
    }
}

@Preview
@Composable
fun ReceivedMessageCardPreview(){
    GwembloChatTheme {
        MessageCard(
            Message(
                messageId = UUID.randomUUID(),
                recipientId = "id1",
                sender = "id2",
                messageNum = 1,
                "test message"
            ), "id1"
        )
    }
}

