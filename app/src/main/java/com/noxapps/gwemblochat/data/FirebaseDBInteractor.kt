package com.noxapps.gwemblochat.data

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class FirebaseDBInteractor {
    companion object {
        const val MESSAGES = "Messages"
        const val MESSAGESREQS = "MessageRequests"

        const val USERS = "Users"
        private val firebaseDB = Firebase.database.reference
        fun upsertUser(user: User){
            firebaseDB
                .child(USERS)
                .child(user.email.replace(".", ""))
                .setValue(user)
        }
        fun getUserByEmail(
            email:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, user:User)->Unit,
        ){
            firebaseDB
                .child(USERS)
                .child(email.replace(".", ""))
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pu ->
                    Log.d("PulledUserRaw", pu.toString())
                    Log.d("PulledUserRawKids", pu.children.toString())
                    Log.d("PulledUserRawRef", pu.ref.toString())


                    val pulledUser = pu.getValue(User::class.java)
                    if (pulledUser==null){
                        Log.d("PulledUser", "failed null check")
                        onFail?.invoke(Exception("User not found"))
                    }
                    else {
                        Log.d("PulledUser", pulledUser.toString())
                        onSuccess(pu, pulledUser)
                    }
                    //onSuccess?.invoke(it)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }
        }

        fun upsertMessage(
            message:Message,
        ){
            //insert Message
            firebaseDB
                .child(MESSAGES)
                .child(message.recipientId)
                .child(message.messageId.toString())
                .setValue(message)

        }
        fun upsertMessageRequest(
            message:Message,
            sender:User
        ){
            //insert Message
            firebaseDB
                .child(MESSAGESREQS)
                .child(message.recipientId)
                .child(message.messageId.toString())
                .child("message")
                .setValue(message)

            firebaseDB
                .child(MESSAGESREQS)
                .child(message.recipientId)
                .child(message.messageId.toString())
                .child("user")
                .setValue(sender)

        }

        fun attachMessageListener(
            auth: FirebaseAuth,
            db: AppDatabase,
            coroutineScope: CoroutineScope
        ) {
            val messageListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Log.w("Message Listener", "loadPost:onDataChange - started", )

                    //val message = dataSnapshot.children.first().getValue(Message::class.java)
                    //Log.w("Message Listener", "reading message" )
                    //Log.w("Message Listener", "dataSnapshot: $dataSnapshot" )
                    //Log.w("Message Listener", "dataSnapshot.children: ${dataSnapshot.children}" )

                    //Log.w("Message Listener", "dataSnapshot.children.first(): ${dataSnapshot.children.first()}" )

                    if(dataSnapshot.hasChildren()){
                        val message = dataSnapshot.children.first().getValue(Message::class.java)?.let { Message(it) }
                        coroutineScope.launch {
                            message?.let{
                                db.messageDao().insert(it)
                                val chat = db.chatDao().getChatByIds(it.sender, it.recipientId)
                                db.chatDao().update(Chat(chat, it.messageId))
                            }
                        }

                    }




                    //dataSnapshot.children.first().ref.removeValue()

                    Log.w("Message Listener", "loadPost:onDataChange - finished" )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("Message Listener", "loadPost:onCancelled", databaseError.toException())
                }
            }
            firebaseDB
                .child(MESSAGES)
                .child(auth.currentUser!!.uid)
                .addValueEventListener(messageListener)
        }

        fun attachMessageRequestListener(
            auth: FirebaseAuth,
            db: AppDatabase,
            coroutineScope: CoroutineScope
        ){
            val messageListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Log.w("Message request Listener", "loadPost:onDataChange - started", )
                    //val message = dataSnapshot.children.first().getValue(Message::class.java)
                    //Log.w("Message Listener", "reading message requests" )
                   // Log.w("Message Listener", "dataSnapshot: $dataSnapshot" )
                    //Log.w("Message Listener", "dataSnapshot.children: ${dataSnapshot.children}" )

                    //Log.w("Message Listener", "dataSnapshot.children.first(): ${dataSnapshot.children.first()}" )
                    if(dataSnapshot.hasChildren()){
                        val messageRequest = dataSnapshot.children.first().getValue(MessageRequest::class.java)
                        messageRequest?.let {

                            coroutineScope.launch {
                                val newMessage = Message(it.message)

                                try {
                                    db.messageDao().insert(newMessage)
                                    val chat = auth.currentUser?.let {
                                        Chat(
                                            ownerId = it.uid,
                                            partnerId = messageRequest.user.userId,
                                            lastMessageId = newMessage.messageId,
                                            activated = false
                                        )
                                    }
                                    try{
                                        db.userDao().insert(it.user)
                                    }
                                    catch (e: Exception){
                                        Log.d("messageRequester", "failed to insert, $e")
                                    }
                                    chat?.let { db.chatDao().insert(it) }
                                    MainScope().launch{
                                        dataSnapshot.children.first().ref.removeValue()
                                    }
                                }
                                catch (e: Exception){
                                    Log.d("messageRequester", "failed to insert, $e")
                                }
                            }
                        }




                    }



                    Log.w("Message request Listener", "loadPost:onDataChange - finished" )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("Message Listener", "loadPost:onCancelled", databaseError.toException())
                }
            }
            firebaseDB
                .child(MESSAGESREQS)
                .child(auth.currentUser!!.uid)
                .addValueEventListener(messageListener)
        }

        fun getMessage( //todo: rewrite with listener
            userId:String,
            giftId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, gift:Message)->Unit,
            ){
            firebaseDB
                .child(MESSAGES)
                .child(userId)
                .child(giftId)
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pg ->
                    val pulledMessage = pg.getValue(Message::class.java)
                    onSuccess(pg, pulledMessage!!)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }

        }

        fun getAllMessages( //todo: rewrite with listener
            userId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, gifts:List<Message>)->Unit,
        ){
            firebaseDB
                .child(userId)
                .child("Gifts")
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pulledMessages ->
                    val message = pulledMessages.children.mapNotNull { it.getValue(Message::class.java) }
                    onSuccess(pulledMessages, message)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }

        }
    }
}
