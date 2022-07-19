package com.mg.handyman.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_table")
data class Job(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "jobTitle_column")
    var jobTitle: String = "",

    @ColumnInfo(name = "specialistName_column")
    var specialistName: String = "",

    @ColumnInfo(name = "rate_column")
    var rate: Double = 0.0,

    @ColumnInfo(name = "duration_column")
    var duration: Double = 0.0

)
