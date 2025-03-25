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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FirebaseDBInteractor {
    companion object {
        const val MESSAGES = "Messages"
        const val USERS = "Users"
        private val firebaseDB = Firebase.database.reference
        fun upsertUser(user: User){
            firebaseDB
                .child(USERS)
                .child(user.email)
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
                .child(email)
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pu ->
                    val pulledUser = pu.getValue(User::class.java)
                    onSuccess(pu, pulledUser!!)
                    //onSuccess?.invoke(it)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }
        }

        fun upsertMessage(
            recipientId:String,
            message:Message,
            listIds:List<Int> = listOf()
        ){
            //insert Message
            firebaseDB
                .child(MESSAGES)
                .child(recipientId)
                .child(message.messageId.toString())
                .setValue(message)

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
                    var counter = 0
                    while(dataSnapshot.hasChildren()&&counter<10) {
                        val message = dataSnapshot.getValue(Message::class.java)
                        coroutineScope.launch { message?.let{db.messageDao().insert(it) }}
                        Log.w("Message Listener", "reading message" )

                        counter++
                    }
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
