package com.example.room.data.database

import androidx.room.TypeConverter
import com.example.room.data.models.JobType

class Converters {
    @TypeConverter
    fun fromJobType(value: JobType): String {
        return value.name
    }

    @TypeConverter
    fun toJobType(value: String): JobType {
        return JobType.valueOf(value)
    }
}
