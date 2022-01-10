package com.example.rally.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rally.data.datasources.SavingsDao
import com.example.rally.model.SavingsAccount
import com.example.rally.model.typeconverters.DateTimeTypeConverter
import com.example.rally.model.typeconverters.ListTypeConverter

@Database(entities = [SavingsAccount::class], version = 1)
@TypeConverters(ListTypeConverter::class, DateTimeTypeConverter::class)
abstract class RallyRoomDatabase : RoomDatabase() {

    abstract fun savingsDao(): SavingsDao

}