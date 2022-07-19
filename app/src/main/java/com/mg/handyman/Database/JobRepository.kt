package com.mg.handyman.Database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class JobRepository(private val jobDatabaseDao: JobDatabaseDao) {
    val allJobs: Flow<List<Job>> = jobDatabaseDao.getAllJobs()
    private lateinit var job: Job

    fun insert(job: Job){
        CoroutineScope(IO).launch {
            jobDatabaseDao.insertJob(job)
        }
    }

    fun getJob(id: Long): Job{
        job = runBlocking { jobDatabaseDao.getJob(id) }
        return job
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch { jobDatabaseDao.deleteJob(id) }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch { jobDatabaseDao.deleteAll() }
    }
}