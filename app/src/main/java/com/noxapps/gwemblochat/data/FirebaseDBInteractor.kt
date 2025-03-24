package com.noxapps.gwemblochat.data

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDBInteractor {
    companion object {
        private val firebaseDB = Firebase.database.reference
        fun upsertUser(user: User){
            firebaseDB
                .child(user.userId)
                .child("User")
                .setValue(user)
        }
        fun getUser(
            userId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, user:User)->Unit,
        ){
            firebaseDB
                .child(userId)
                .child("User")
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
            userId:String,
            gift:Message,
            listIds:List<Int> = listOf()
        ){
            //insert gift
            firebaseDB
                .child(userId)
                .child("Messages")
                .child(gift.messageId.toString())
                .setValue(gift)
            //delete old relationships
            /*firebaseDB
                .child(userId)
                .child("Relationships")
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    dataSnapshot.children.forEach { child ->
                        child.key?.let {
                            firebaseDB
                                .child(userId)
                                .child("Relationships")
                                .child(it)
                                .child(gift.giftId.toString())
                                .removeValue()
                        }
                    }
                }

            //insert new relationships
            listIds.forEach { listId ->
                firebaseDB
                    .child(userId)
                    .child("Relationships")
                    .child(listId.toString())
                    .child(gift.giftId.toString())
                    .setValue(true)
            }*/

        }
        fun getMessage(
            userId:String,
            giftId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, gift:Message)->Unit,
            ){
            firebaseDB
                .child(userId)
                .child("Gifts")
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

        fun getAllMessages(
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


        /*fun deleteMessage(
            userId: String,
            message: Message,
            ) {
            firebaseDB
                .child(userId)
                .child("Gifts")
                .child(message.gift.giftId.toString())
                .removeValue()

            message.lists.forEach { list ->
                firebaseDB
                    .child(userId)
                    .child("Relationships")
                    .child(list.listId.toString())
                    .child(message.gift.giftId.toString())
                    .removeValue()
            }
        }

        fun upsertList(
            userId:String,
            list:GiftList,
            giftIds:List<Int> = listOf()
        ){
            //insert list
            firebaseDB
                .child(userId)
                .child("User")
                .child(list.listId.toString())
                .setValue(list)

            //delete old relationships
            firebaseDB
                .child(userId)
                .child("Relationships")
                .child(list.listId.toString())
                .removeValue()

            //insert current relationships
            giftIds.forEach { giftId ->
                firebaseDB
                    .child(userId)
                    .child("Relationships")
                    .child(list.listId.toString())
                    .child(giftId.toString())
                    .setValue(true)
            }

        }

        fun getList(
            userId:String,
            listId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, giftList:GiftList)->Unit
        ){
            firebaseDB
                .child(userId)
                .child("Gifts")
                .child(listId)
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pl ->
                    val pulledList = pl.getValue(GiftList::class.java)
                    onSuccess(pl, pulledList!!)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }
        }

        fun getAllLists(
            userId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, lists:List<GiftList>)->Unit,
        ){
            firebaseDB
                .child(userId)
                .child("Lists")
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pg ->
                    val lists = pg.children.mapNotNull { it.getValue(GiftList::class.java) }
                    onSuccess(pg, lists)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }

        }

        fun deleteList(
            userId: String,
            listObject: ListWithGifts,
        ) {
            firebaseDB.child(userId)
                .child("Lists")
                .child(listObject.giftList.listId.toString())
                .removeValue()
            listObject.gifts.forEach { gift ->
                firebaseDB.child(userId).child("Relationships")
                    .child(listObject.giftList.listId.toString())
                    .child(gift.giftId.toString())
                    .removeValue()
            }
        }

        fun getAllRelationships( //this is incorrect
            userId:String,
            onFail:((exception:Exception)->Unit)? = null,
            onComplete: ((task: Task<DataSnapshot>)->Unit)? = null,
            onSuccess:(result: DataSnapshot, references:List<GiftListGiftCrossReference>)->Unit,
        ){
            firebaseDB
                .child(userId)
                .child("Relationships")
                .get()
                .addOnFailureListener(){
                    onFail?.invoke(it)
                }
                .addOnSuccessListener() {pr ->
                    val references = mutableListOf<GiftListGiftCrossReference>()
                    pr.children.mapNotNull { list ->
                        val listId = list.key?.toInt()?:0
                        for(entry in list.children){
                            val result = entry.getValue(Boolean::class.java)
                            result?.let {
                                if (it) {
                                    val giftId = entry.key?.toInt()?:0
                                    references.add(GiftListGiftCrossReference(listId, giftId))
                                }
                            }
                        }

                    }
                    onSuccess(pr, references)
                }
                .addOnCompleteListener(){
                    onComplete?.invoke(it)
                }

        }
        */

    }
}
