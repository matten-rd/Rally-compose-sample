package com.example.rally.model.typeconverters

import androidx.room.TypeConverter
import com.example.rally.model.SavingsAccountData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ListTypeConverter {

    @TypeConverter
    fun fromList(value: List<SavingsAccountData>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<SavingsAccountData>>(value)

}