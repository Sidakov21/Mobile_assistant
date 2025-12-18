package com.example.mobileassistant.data.local.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Converters {



    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Для преобразования Long -> LocalDateTime (если нужно для UI)
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }

    // Для преобразования LocalDateTime -> Long (если нужно для сохранения)
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromString(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToString(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}
