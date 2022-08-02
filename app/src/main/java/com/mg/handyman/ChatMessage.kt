package com.mg.handyman

import com.google.firebase.Timestamp

data class ChatMessage (
    val date: Timestamp = Timestamp(java.util.Date()),
    val text: String = "",
    val from: String = "",
    val to: String = "",
    val fromId: String = "",
    val toId: String = ""
)