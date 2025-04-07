package com.noxapps.gwemblochat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface ReferenceDao {
    @Insert
    suspend fun insertAll(vararg references: MissedMessageCrossReference)

    @Insert
    suspend fun insert(reference: MissedMessageCrossReference)

    @Upsert
    suspend fun upsert(reference: MissedMessageCrossReference)

    @Update
    suspend fun update(vararg references:MissedMessageCrossReference)

    @Delete
    suspend fun delete(reference: MissedMessageCrossReference)

    @Query("SELECT * FROM MissedMessageCrossReference")
    suspend fun getAll(): List<MissedMessageCrossReference>

    @Query("SELECT * FROM MissedMessageCrossReference WHERE chatId = :id")
    suspend fun getByListId(id:Int):List<MissedMessageCrossReference>

    @Query("SELECT * FROM MissedMessageCrossReference WHERE messageId = :id")
    suspend fun getByGiftId(id:Int):List<MissedMessageCrossReference>

}