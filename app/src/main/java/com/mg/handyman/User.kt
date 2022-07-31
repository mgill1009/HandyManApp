package com.mg.handyman

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val username: String = "",
    val uid: String = "",
): Parcelable