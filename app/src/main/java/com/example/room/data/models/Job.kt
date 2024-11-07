package com.example.room.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val dateFrom: String,
    val dateTo: String,
    val location: String,
    val jobType: JobType
)

enum class JobType {
    PAINTING,
    WALLPAPERING,
    BOTH
}
