package com.noxapps.gwemblochat.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.gwemblochat.R
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndLastMessage
import com.noxapps.gwemblochat.navigation.Paths

@Composable
fun ChatCard(chat: ChatWithUserAndLastMessage, navController: NavHostController){
    var maxHeight by remember{ mutableIntStateOf(0) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp, 10.dp)
        //.height(IntrinsicSize.Min)
        .onGloballyPositioned { coordinates->
            if(maxHeight==0)maxHeight = coordinates.size.height
        }
        .clickable{
            navController.navigate("${Paths.Chat.Path}/${chat.chat.id}")
        }
    ){
        Image(
            painter = painterResource(R.drawable.default_pfp),
            contentDescription = "ProfilePic",
            modifier = Modifier.height(with(LocalDensity.current) { maxHeight.toDp() })

        )
        Column(modifier = Modifier.padding(8.dp, 0.dp)){
            Row(){
                Text(
                    text = chat.user.userName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "date",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = chat.lastMessage.plainText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge

            )
        }

    }

}