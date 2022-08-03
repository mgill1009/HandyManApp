package com.mg.handyman

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var sendButton: AppCompatImageButton
    private lateinit var messageEditText: EditText
    private lateinit var adapter: ChatArrayAdapter
    private lateinit var chats: ArrayList<Chat>

    private lateinit var otherUser: User
    private lateinit var currentUser: User

    private lateinit var auth: FirebaseAuth

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth

        val bundle = intent.extras
        otherUser = bundle!!.getParcelable(MessageListActivity.NAME_KEY)!!

        supportActionBar?.title = otherUser.username.replace("[", "")
            .replace("]", "").substringAfter(", ")

        currentUser = User(auth.currentUser!!.displayName.toString(),
            auth.currentUser!!.uid
        )

        listView = findViewById(R.id.chatListView)

        chats = ArrayList()
        adapter = ChatArrayAdapter(chats, this, currentUser, 1)
        listView.adapter = adapter

        val chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatViewModel.currentUser = currentUser
        chatViewModel.otherUser = otherUser
        chatViewModel.mutableChat.observe(this) {
            chats = it as ArrayList<Chat>
            adapter = ChatArrayAdapter(chats, this, currentUser, 1)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        messageEditText = findViewById(R.id.messageInputET)

        sendButton = findViewById(R.id.sendBtn)
        sendButton.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {
        val text = messageEditText.text.toString()
        if (text != "") {
            val fromId = currentUser.uid
            val from = currentUser.username
            val toId = otherUser.uid
            val to = otherUser.username
            val date = Timestamp(java.util.Date())
            val messageData = hashMapOf(
                "text" to text,
                "fromId" to fromId,
                "toId" to toId,
                "from" to from,
                "to" to to,
                "date" to date
            )
            db.collection("messages")
                .add(messageData)
                .addOnSuccessListener {
                    messageEditText.setText("")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_LONG).show()
                }
        }
    }
}

// a message and the user that sent the message
class Chat(
    val message: String,
    val user: User,
    val id: String,
    val date: Any?,
    val to: Any?,
    val from: Any?
) {

}