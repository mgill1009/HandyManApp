package com.mg.handyman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.common.io.Files.append
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_message_list.*

class MessageListActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var users: MutableList<User>
    private lateinit var auth: FirebaseAuth
    companion object {
        val NAME_KEY = "NAME_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        auth = Firebase.auth

        supportActionBar?.title = "Messages"

        getUsers()
    }

    private fun getUsers() {
        val tmpArr: MutableList<User> = ArrayList()

        val query = db.collection("messages").whereEqualTo("fromId", auth.currentUser?.uid)

        val query2 = db.collection("messages").whereEqualTo("toId", auth.currentUser?.uid)

        // updates when db changes
        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // if there is an exception create a toast and close messagelist
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
            }
            querySnapshot?.let {

                for (message in it) {
                    val text = message.data["text"]
                    val to = message.data["to"]
                    val from = message.data["from"]
                    val toId = message.data["toId"]
                    val fromId = message.data["fromId"]

                    Log.d("debug", "values are: $text")

                    if(toId != auth.currentUser?.uid){
                        if(!tmpArr.contains(User(to as String, toId as String)))
                            tmpArr.add(User(to, toId))
                    }else if(fromId != auth.currentUser?.uid){
                        if(!tmpArr.contains(User(from as String, fromId as String)))
                            tmpArr.add(User(from, fromId))
                    }

                    //val id = user.data.values.first()
                    //val name = user.data.values.last()
                    //tmpArr.add(User(name as String, id as String))
                    Log.d("debug", "tmp arr is ${tmpArr}")
                }
                users = tmpArr
                val userNames = tmpArr.map { it.username.replace("[", "").
                                    replace("]", "").substringAfter(", ") }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNames)
                listViewMessages.adapter = adapter
                listViewMessages.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, _ ->
//                    val user = parent.getItemAtPosition(position) as User
                    val user = users[position]
                    val intent = Intent(view.context, ChatActivity::class.java)

                    val bundle = Bundle()
                    bundle.putParcelable(NAME_KEY, user)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }

        }

        // Query 2

        query2.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // if there is an exception create a toast and close messagelist
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
            }
            querySnapshot?.let {

                for (message in it) {
                    val text = message.data["text"]
                    val to = message.data["to"]
                    val from = message.data["from"]
                    val toId = message.data["toId"]
                    val fromId = message.data["fromId"]

                    Log.d("debug", "values are: $text")

                    if(toId != auth.currentUser?.uid){
                        if(!tmpArr.contains(User(to as String, toId as String)))
                            tmpArr.add(User(to, toId))
                    }else if(fromId != auth.currentUser?.uid){
                        if(!tmpArr.contains(User(from as String, fromId as String)))
                            tmpArr.add(User(from, fromId))
                    }

                    //val id = user.data.values.first()
                    //val name = user.data.values.last()
                    //tmpArr.add(User(name as String, id as String))
                    Log.d("debug", "tmp arr is ${tmpArr}")
                }
                users = tmpArr
                val userNames = tmpArr.map { it.username.replace("[", "").
                replace("]", "").substringAfter(", ") }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNames)
                listViewMessages.adapter = adapter
                listViewMessages.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, _ ->
//                    val user = parent.getItemAtPosition(position) as User
                    val user = users[position]
                    val intent = Intent(view.context, ChatActivity::class.java)

                    val bundle = Bundle()
                    bundle.putParcelable(NAME_KEY, user)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }

        }

    }
}