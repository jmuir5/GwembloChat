package com.noxapps.gwemblochat.data

import androidx.room.TypeConverter

class TypeConverters{
    @TypeConverter
    fun fromByteArray(byteArray:ByteArray):String{
        return byteArray.toString()
    }
    @TypeConverter
    fun toByteArray(string:String):ByteArray{
        return string.toByteArray()
    }
}