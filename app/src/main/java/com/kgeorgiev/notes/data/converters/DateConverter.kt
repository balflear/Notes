package com.kgeorgiev.notes.data.converters

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by kostadin.georgiev on 9/19/2019.
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}