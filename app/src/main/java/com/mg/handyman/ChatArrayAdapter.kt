package com.mg.handyman

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

internal class ChatArrayAdapter(private var chatList: Array<Chat>,
                                private val activity: FragmentActivity,
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
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.chat_row, null)
        textView = view.findViewById(R.id.chatItemTV)
        var finalStr = ""
        finalStr += chatList[position].user.username
        finalStr += ": "
        finalStr += chatList[position].message
        textView.text = finalStr
        return view
    }
}