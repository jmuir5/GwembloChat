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
    suspend fun insertAll(vararg references: UserMessageCrossReference)

    @Insert
    suspend fun insert(reference: UserMessageCrossReference)

    @Upsert
    suspend fun upsert(reference: UserMessageCrossReference)

    @Update
    suspend fun update(vararg references:UserMessageCrossReference)

    @Delete
    suspend fun delete(reference: UserMessageCrossReference)

    @Query("SELECT * FROM UserMessageCrossReference")
    suspend fun getAll(): List<UserMessageCrossReference>

    @Query("SELECT * FROM UserMessageCrossReference WHERE userId = :id")
    suspend fun getByListId(id:Int):List<UserMessageCrossReference>

    @Query("SELECT * FROM UserMessageCrossReference WHERE messageId = :id")
    suspend fun getByGiftId(id:Int):List<UserMessageCrossReference>

}