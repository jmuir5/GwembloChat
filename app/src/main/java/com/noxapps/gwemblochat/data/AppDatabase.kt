package com.noxapps.gwemblochat.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Message::class, User::class, UserMessageCrossReference::class], version = 1) //
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userDao():UserDao
    abstract fun referenceDao():ReferenceDao
}