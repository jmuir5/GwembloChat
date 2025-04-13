package com.noxapps.gwemblochat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.noxapps.gwemblochat.data.Relationships.ChatWithUserAndAllMessages
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    suspend fun insertAll(vararg messages: Message)

    @Insert
    suspend fun insert(message: Message):Long

    @Upsert
    suspend fun upsert(message: Message):Long

    @Update
    suspend fun update(vararg messages: Message)

    @Delete
    suspend fun delete(message: Message)

    @Query("SELECT * FROM Message")
    suspend fun getAll(): List<Message>

    @Transaction
    @Query("SELECT * FROM Message where remoteId = :id")
    fun getAllMessagesByRemoteId(id: String): Flow<List<Message>>

    @Transaction
    @Query("SELECT * FROM Message where remoteId = :id")
    suspend fun getAllMessagesByLocalId(id: String): List<Message>

}