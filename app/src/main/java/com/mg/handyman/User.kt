package com.mg.handyman

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data class to model a user in the database
 * Used in ChatActivity
 */

@Parcelize
data class User(
    val username: String = "",
    val uid: String = "",
): Parcelable