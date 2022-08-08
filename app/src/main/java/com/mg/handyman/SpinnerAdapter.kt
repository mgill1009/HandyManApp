package com.mg.handyman

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

/**
 * Adapter class to populate images representing job type when user is posting a new job
 */

class SpinnerAdapter(var context: Context, var images: ArrayList<Int>) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }


    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if(view == null){
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(R.layout.content_spinner, parent, false)
        }

        val imgView: ImageView = view!!.findViewById(R.id.imgView) as ImageView
        imgView.setImageResource(images[position])
        return view
    }

}