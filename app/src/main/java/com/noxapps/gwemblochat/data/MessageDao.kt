package com.noxapps.gwemblochat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

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
}