package com.mg.handyman

data class Conversation (
    val messages: ArrayList<ChatMessage> = ArrayList(),
    val createdAt: String = "",
    val participants: ArrayList<ChatMessage> = ArrayList(),
    val updatedAt: String = ""
)