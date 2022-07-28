package com.mg.handyman

data class Job(
    val title: String = "",
    val specialistName: String = "",
    val price: Double = 0.0,
    val duration: Double = 0.0,
    val phone: Long = 0L,
    val description: String = "",
    val location: String = ""
)
