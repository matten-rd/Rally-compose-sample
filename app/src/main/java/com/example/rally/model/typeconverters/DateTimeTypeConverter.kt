package com.example.rally.model.typeconverters

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object DateTimeTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

}