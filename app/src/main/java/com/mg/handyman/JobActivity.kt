package com.mg.handyman

/**
 * Shows a particular job details
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_job.*
import java.text.DecimalFormat

class JobActivity : AppCompatActivity() {
    private lateinit var model: Job
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var serviceInfoTextView: TextView
    private lateinit var messageButton: Button
    private val decimalFormat = DecimalFormat("0.#")

    companion object{
        private const val TAG = "JobActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job)

        model = intent.getParcelableExtra<Job>(MainActivity.SELECTED_JOB) as Job

        Log.d(TAG, "Got ${model.title}")

        titleTextView = findViewById(R.id.title_tv)
        descriptionTextView = findViewById(R.id.description_tv)
        infoTextView = findViewById(R.id.info_tv)
        serviceInfoTextView = findViewById(R.id.service_info_tv)
        messageButton = findViewById(R.id.messageLister_btn)

        titleTextView.text = model.title
        val info = "${model.specialistName}   |  ${model.location}   |  ${model.phone}"
        infoTextView.text = info
        val serviceInfo = "$${decimalFormat.format(model.price)} per hour    |   Estimated time: ${decimalFormat.format(model.duration)} hours"
        serviceInfoTextView.text = serviceInfo
        descriptionTextView.text = model.description

//        //Message lister activity
//        messageButton.setOnClickListener {
//            val intent = Intent(this, MessageListActivity::class.java)
//            startActivity(intent)
//        }
    }


}