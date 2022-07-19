package com.mg.handyman.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Job::class], version = 1)
abstract class JobDatabase: RoomDatabase() {
    abstract fun jobDatabaseDao(): JobDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: JobDatabase? = null

        // creates and returns a new instance of one doesn't exist
        fun getInstance(context: Context): JobDatabase{
            synchronized(this){
                var db_instance = INSTANCE
                if(db_instance == null){
                    db_instance = Room.databaseBuilder(
                        context.applicationContext, JobDatabase::class.java, "job_table"
                    ).build()
                    INSTANCE = db_instance
                }
                return db_instance
            }
        }
    }
}