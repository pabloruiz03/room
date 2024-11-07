package com.example.room.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.room.data.models.Job

@Dao
interface JobDao {
    @Insert
    suspend fun insert(job: Job) // Este método sí debe ser suspend

    @Query("SELECT * FROM jobs ORDER BY dateFrom ASC")
    fun getAllJobs(): kotlinx.coroutines.flow.Flow<List<Job>>

}

