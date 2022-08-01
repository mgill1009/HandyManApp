package com.mg.handyman

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var sendButton: AppCompatImageButton
    private lateinit var messageEditText: EditText
    private lateinit var adapter: ChatArrayAdapter
    private lateinit var chatArrayList: ArrayList<Chat>

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

        chatArrayList = arrayListOf()

        adapter = ChatArrayAdapter(chatArrayList, this, 1)

        initMessageListener()

        listView = findViewById(R.id.chatListView)
        listView.adapter = adapter

        messageEditText = findViewById(R.id.messageInputET)

        sendButton = findViewById(R.id.sendBtn)
        sendButton.setOnClickListener {
            sendMessage()
        }

    }

    private fun initMessageListener() {
        val query = db.collection("messages")
        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // if there is an exception create a toast and close messagelist
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
            }
            querySnapshot?.let {
                for (message in it) {
                    val data = message.data as HashMap<String, Any>
                    if (currentUser.uid == data["toId"] && otherUser.uid == data["fromId"]) {
                        // if the message is already in the list then do not add it
                        if ((chatArrayList.filter { chat -> chat.id == message.id }).isNotEmpty()) {
                            continue
                        } else {
                            val msg = Chat(data["text"] as String,
                                User(otherUser.username, otherUser.uid),
                                message.id)
                            chatArrayList.add(msg)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else if (currentUser.uid == data["fromId"] && otherUser.uid == data["toId"]) {
                        // if the message is already in the list then do not add it
                        if ((chatArrayList.filter { chat -> chat.id == message.id }).isNotEmpty()) {
                            continue
                        } else {
                            val msg = Chat(data["text"] as String,
                                User(currentUser.username, currentUser.uid),
                                message.id)
                            chatArrayList.add(msg)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }


    private fun sendMessage() {
        val text = messageEditText.text.toString()
        if (text != "") {
            val fromId = currentUser.uid
            val toId = otherUser.uid
            val date = Timestamp(java.util.Date())
            val messageData = hashMapOf(
                "text" to text,
                "fromId" to fromId,
                "toId" to toId,
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
class Chat(val message: String, val user: User, val id: String) {

}