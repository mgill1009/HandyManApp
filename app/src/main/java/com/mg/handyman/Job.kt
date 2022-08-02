package com.mg.handyman

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Job(
    val title: String = "",
    val specialistName: String = "",
    val price: Double = 0.0,
    val duration: Double = 0.0,
    val phone: Long = 0L,
    val description: String = "",
    val location: String = "Burnaby, BC",
    val pictureId: Int = R.drawable.job,
    val uid: String = ""
): Parcelable
