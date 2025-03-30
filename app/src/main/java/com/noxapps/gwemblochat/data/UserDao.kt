package com.noxapps.gwemblochat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface UserDao {
    @Insert
    suspend fun insertAll(vararg users: User)

    @Insert
    suspend fun insert(user: User):Long

    @Upsert
    suspend fun upsert(user: User):Long

    @Update
    suspend fun update(vararg users: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Query ("SELECT * FROM User WHERE userId = :id LIMIT 1")
    suspend fun getOneById (id: String) : User

    @Query ("SELECT * FROM User WHERE email = :email LIMIT 1")
    suspend fun getOneByEmail(email: String) : User


    /*@Transaction
    @Query("SELECT * FROM Gift")
    suspend fun getGiftsWithLists(): List<GiftWithLists>

    @Transaction
    @Query("SELECT * FROM Gift WHERE giftId = :id LIMIT 1")
    suspend fun getOneWithListsById(id: Int): GiftWithLists*/
}