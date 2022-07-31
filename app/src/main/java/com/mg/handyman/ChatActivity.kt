package com.mg.handyman

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var sendButton: AppCompatImageButton

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth

        val bundle = intent.extras
        val user = bundle!!.getParcelable<User>(MessageListActivity.NAME_KEY)

        if (user != null) {
            supportActionBar?.title = user.username
        }

        val currentUser = User(auth.currentUser!!.displayName.toString(),
            auth.currentUser!!.uid
        )

        val arrayList = arrayOf(
            Chat("message1", user!!),
            Chat("message2", user),
            Chat("message1", currentUser)
        )

        val adapter = ChatArrayAdapter(arrayList, this, 1)

        listView = findViewById(R.id.chatListView)
        listView.adapter = adapter

        sendButton = findViewById(R.id.sendBtn)
        sendButton.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {

    }
}

class Chat(val message: String, val user: User) {

}