package com.noxapps.gwemblochat.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.gwemblochat.crypto.ECDH
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.Chat
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import com.noxapps.gwemblochat.data.Message
import com.noxapps.gwemblochat.data.User
import com.noxapps.gwemblochat.data.toB64String
import com.noxapps.gwemblochat.navigation.Paths
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun NewChatPage(
    navController: NavController,
    db: AppDatabase,
    coroutineScope: CoroutineScope,
    auth: FirebaseAuth,
    user:User,
    context: Context
){
    val searchTerm = remember{mutableStateOf("")}
    val firstMessage = remember{mutableStateOf("")}
    var enabled by remember{mutableStateOf(true)}
    var recipient = remember{mutableStateOf<User?>(null)}
    //val noUser by remember{derivedStateOf { recipient.value == User() }}


    Column(modifier = Modifier){
        if(recipient.value == null) {
            SearchInput(
                message = searchTerm,
                enabled = enabled,
                onSearch = {
                    if(searchTerm.value.replace(".", "") == auth.currentUser?.email?.replace(".", "")){
                        Toast.makeText(
                            context,
                            "Hey, Thats Me!",
                            Toast.LENGTH_SHORT
                        ).show()
                        searchTerm.value = ""
                    }
                    else {
                        val searchHolder = searchTerm.value
                        Log.d("NewChatSearch Term", searchTerm.value)

                        enabled = false
                        coroutineScope.launch {
                            val localUser: User = db.userDao().getOneByEmail(searchHolder)
                            val existingChat:Chat? =
                                try{
                                    auth.currentUser?.let { db.chatDao().getChatByIds(it.uid, localUser.userId) }
                                }
                                catch (e:Exception){
                                    null
                                }
                            if(existingChat != null) {
                                Log.d("NewChatPage", "existing chat: $existingChat")
                                navController.navigate("${Paths.Chat.Path}/${existingChat.id}"){
                                    //todo pop last page
                                }
                            }
                            MainScope().launch {
                                Log.d("NewChatPage", "localUser: $localUser")
                                Log.d("NewChatPage", "localUser: ${user.userId}")
                                Log.d("NewChatPage", "localUser: ${user.userName}")
                                Log.d("NewChatPage", "localUser: ${user.email}")
                                if (localUser != null) {
                                    recipient.value = localUser
                                    //return@launch
                                } else {
                                    Log.d("NewChatSearch Term", searchHolder)

                                    FirebaseDBInteractor.getUserByEmail(
                                        email = searchHolder,
                                        onFail = {
                                            //onFail
                                            Toast.makeText(
                                                context,
                                                "User not found",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            enabled = true
                                        }
                                    ) { _, user ->
                                        Log.d("NewChatPage", "user found: $user")
                                        Log.d("NewChatPage", "user found: ${user.userId}")
                                        Log.d("NewChatPage", "user found: ${user.userName}")
                                        Log.d("NewChatPage", "user found: ${user.email}")
                                        recipient.value = user
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
        else {
            NewChatUserCard(user = recipient)

            Spacer(modifier = Modifier.weight(1f))
            StyledInput(
                message = firstMessage,
                enabled = true,
                labelText = "Message"
            ) {

                val newChat = recipient.value?.let { recipient ->
                    auth.currentUser?.let {
                        var crappySecretKey = "32 insecure bits".toByteArray()
                        (crappySecretKey.size..32).map{crappySecretKey+=0}

                        Chat.initNewChat(
                            ownerId = it.uid,
                            partnerId = recipient.userId,
                            partnerDHPublicKey = recipient.identityPublicKey,
                            secretKey = crappySecretKey//ECDH.doECDH(user.identityPrivateKey, recipient.identityPublicKey)
                        )
                    }
                }
                val encryptedMessage = newChat?.let {
                    ECDH.ratchetEncrypt(
                        it,
                        firstMessage.value,
                        user.identityPrivateKey + recipient.value!!.identityPublicKey,
                        db,
                        coroutineScope
                    )
                }
                val message = auth.currentUser?.let {currentUser ->
                    recipient.value?.let { recipient ->
                        Message(
                            remoteId = recipient.userId,
                            recipientId = recipient.userId,
                            sender = currentUser.uid,
                            messageNum = 0,
                            plainText = firstMessage.value,
                            _dhPublicKey = newChat!!.selfDiffieHellmanPublic.toB64String(),
                            chainLength = 0
                        )
                    }
                }

                val sentMessage = auth.currentUser?.let {currentUser ->
                    recipient.value?.let { recipient ->
                        Message(
                            remoteId = currentUser.uid,
                            recipientId = recipient.userId,
                            sender = currentUser.uid,
                            _cypherText = encryptedMessage!!.second.toB64String(),
                            messageNum = 0,
                            _dhPublicKey = newChat.selfDiffieHellmanPublic.toB64String(),
                            chainLength = 0
                        )
                    }
                }
                sentMessage?.let {
                    FirebaseDBInteractor.upsertMessageRequest(
                        message = it,
                        sender = user
                    )
                }
                coroutineScope.launch {
                    try{recipient.value?.let{db.userDao().insert(it)}}
                    catch (e:Exception){
                        Log.d("NewChatPage", "error inserting user: $e")
                    }
                    message?.let {
                        db.messageDao().insert(it)
                        newChat?.let { chat ->
                            chat.lastMessageId = it.messageId
                            val newId = db.chatDao().insert(chat).toInt()
                            Log.d("NewChatPage", "chatID: $chat.id")

                            MainScope().launch{
                                navController.navigate("${Paths.Chat.Path}/${newId}"){
                                    //todo pop last page
                                }
                            }
                        }
                    }


                }




            }
        }
    }
}