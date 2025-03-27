package com.noxapps.gwemblochat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndAllMessages
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndLastMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert
    suspend fun insertAll(vararg chats: Chat)

    @Insert
    suspend fun insert(chat: Chat):Long

    @Upsert
    suspend fun upsert(chat: Chat):Long

    @Update
    suspend fun update(vararg chats: Chat)

    @Delete
    suspend fun delete(chat: Chat)

    @Query("SELECT * FROM Chat")
    fun getAll(): List<Chat>

    @Transaction
    @Query("SELECT * FROM  Chat where ownerId = :ownerId")
    fun getAllChatsWithLastMessage(ownerId:String): Flow<List<ChatWithUserAndLastMessage>>

    @Transaction
    @Query("SELECT * FROM  Chat where partnerId = :id LIMIT 1")
    suspend fun getChatByIdWithAllMessages(id: String): ChatWithUserAndAllMessages



}