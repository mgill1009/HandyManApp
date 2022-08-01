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
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_message_list.*

class MessageListActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var users: MutableList<User>
    companion object {
        val NAME_KEY = "NAME_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        supportActionBar?.title = "Messages"

        getUsers()
    }

    private fun getUsers() {
        val query = db.collection("users")

        // updates when db changes
        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // if there is an exception create a toast and close messagelist
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
            }
            querySnapshot?.let {
                val tmpArr: MutableList<User> = ArrayList()
                for (user in it) {
                    tmpArr.add(User(user.data.values.toString(), user.id))
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