package com.mg.handyman

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel to show chat conversation with a user
 * Observes changes in the chatAdapter and refreshes as new messages are
 * coming
 */

class ChatViewModel : ViewModel() {

    private val db = Firebase.firestore
    lateinit var currentUser: User
    lateinit var otherUser: User
    val chatArrayList = ArrayList<Chat>()
    val mutableChat = MutableLiveData<List<Chat>>()

    init {
        CoroutineScope(IO).launch {
            runOnMainThread()
        }
    }

    private suspend fun runOnMainThread() {
        withContext(Main) {
            initMessageListener()
        }
    }

    private fun initMessageListener() {
        val query = db.collection("messages").orderBy("date")   // sort chat by date created
        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // if there is an exception create a toast and close messagelist
//            firebaseFirestoreException?.let {
//                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                finish()
//            }
            querySnapshot?.let {
                for (message in it) {
                    val data = message.data as HashMap<String, Any>
                    if (currentUser.uid == data["toId"] && otherUser.uid == data["fromId"]) {
                        // if the message is already in the list then do not add it
                        if ((chatArrayList.filter { chat:Chat -> chat.id == message.id }).isNotEmpty()) {
                            continue
                        } else {
                            val msg = Chat(
                                data["text"] as String,
                                User(otherUser.username, otherUser.uid),
                                message.id, data["date"], data["toId"], data["fromId"]
                            )
                            chatArrayList.add(msg)
                            update()
                        }
                    }
                    else if (currentUser.uid == data["fromId"] && otherUser.uid == data["toId"]) {
                        // if the message is already in the list then do not add it
                        if ((chatArrayList.filter { chat -> chat.id == message.id }).isNotEmpty()) {
                            continue
                        } else {
                            val msg = Chat(data["text"] as String,
                                User(currentUser.username, currentUser.uid),
                                message.id, data["date"], data["toId"], data["fromId"])
                            chatArrayList.add(msg)
                            update()
                        }
                    }
                }
            }
        }
    }
    private fun update() {
        mutableChat.value = chatArrayList
    }
}