package com.mg.handyman

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

/**
 * Dialog pop-up that confirms if the user wants to delete their posting
 */

interface onDeleteListener{
    fun deleteListing()
}

class CustomDialogFragment : DialogFragment() {

    private lateinit var listener: onDeleteListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.ask_confirmation)
            builder.setMessage(R.string.alert_delete_posting)
                .setPositiveButton(R.string.delete) { dialog, id ->
                    Log.d("debug", "id is $id")
                    listener.deleteListing()
                }
                .setNegativeButton(R.string.cancel){ dialog, id ->
                    Log.d("debug", "negative button clicked")
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        if(context is onDeleteListener){
            listener = context
        }
        super.onAttach(context)
    }
}