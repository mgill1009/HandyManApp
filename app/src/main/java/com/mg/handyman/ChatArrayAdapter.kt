package com.mg.handyman

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseUser
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

internal class ChatArrayAdapter(private var chatList: ArrayList<Chat>,
                                private val activity: FragmentActivity,
                                private val currentUser : User,
                                private val side: Int): BaseAdapter() {

    private lateinit var textView: TextView

    override fun getCount(): Int {
        return chatList.size
    }

    override fun getItem(p0: Int): Any {
        return chatList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        // set message's side (left/right) by checking it's uid
        if (currentUser.uid == chatList[position].to) {
            val view = View.inflate(activity, R.layout.message_left, null)
            textView = view.findViewById(R.id.tvMessageLeft)
            val finalStr = chatList[position].message
            textView.text = finalStr
            return view
        } else if (currentUser.uid == chatList[position].from) {
            val view = View.inflate(activity, R.layout.message_right, null)
            textView = view.findViewById(R.id.tvMessageRight)
            val finalStr = chatList[position].message
            textView.text = finalStr
            return view
        }
        return null // error
    }
}