package com.mg.handyman

import android.os.Parcelable
import com.google.firebase.Timestamp
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
    val uid: String = "",
    var rating: Double = 0.0,
    var timesRated: Int = 0,
    var titleAsArray: ArrayList<String> = ArrayList(),
    val createdAt: Timestamp? = null
): Parcelable
