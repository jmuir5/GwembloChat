package com.noxapps.gwemblochat.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class, User::class, Chat::class, MissedMessageCrossReference::class], version = 7) //
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userDao():UserDao
    abstract fun chatDao():ChatDao
    abstract fun referenceDao():ReferenceDao
}