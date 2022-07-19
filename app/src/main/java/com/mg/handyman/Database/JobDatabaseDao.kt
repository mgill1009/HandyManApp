package com.mg.handyman.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDatabaseDao {

    // inserts a new job as a new row in the database
    @Insert
    suspend fun insertJob(job: Job)

    // returns a row from database for the provided row id
    @Query("SELECT * FROM job_table WHERE id = :key")
    suspend fun getJob(key: Long): Job

    // returns all rows from the database
    @Query("SELECT * FROM job_table")
    fun getAllJobs(): Flow<List<Job>>

    // deletes a specific row (with row id) from the database
    @Query("DELETE FROM job_table WHERE id = :key")
    suspend fun deleteJob(key: Long)

    // deletes all jobs from the database
    @Query("DELETE FROM job_table")
    suspend fun deleteAll()
}