package com.noxapps.gwemblochat.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noxapps.gwemblochat.R
import com.noxapps.gwemblochat.data.User

@Composable
fun NewChatUserCard(user:MutableState<User?>){
    var maxHeight by remember{ mutableIntStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            //.border(Dp.Hairline, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            //.height(IntrinsicSize.Min)
            ,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 10.dp)
                .onGloballyPositioned { coordinates ->
                    if (maxHeight == 0) maxHeight = coordinates.size.height
                }
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.default_pfp),
                contentDescription = "ProfilePic",
                modifier = Modifier.height(with(LocalDensity.current) { maxHeight.toDp() })

            )
            Column() {
                user.value?.let {
                    Text(
                        text = it.userName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                user.value?.let {
                    Text(
                        text = it.email,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    user.value = null
                }
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    painter = painterResource(id = R.drawable.close_24px),
                    contentDescription = "send",
                )
            }
        }
    }
}