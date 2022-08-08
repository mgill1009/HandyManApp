package com.mg.handyman

/**
 * This activity shows a particular job details
 * User has option to rate a particular listing (except their own) or message the lister
 * If user is viewing their own listing, they have an option to 'delete' listing
 * from this screen
 */

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat


class JobActivity : AppCompatActivity(), onDeleteListener {
    private lateinit var model: Job
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var serviceInfoTextView: TextView
    private lateinit var messageButton: Button
    private val decimalFormat = DecimalFormat("0.#")
    private lateinit var auth: FirebaseAuth

    private lateinit var lister: User
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingButton: Button
    private val db = Firebase.firestore

    companion object{
        private const val TAG = "JobActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_title_1)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        auth = Firebase.auth

        model = intent.getParcelableExtra<Job>(MainActivity.SELECTED_JOB) as Job

        ratingBar = findViewById(R.id.rating_bar)
        ratingButton =  findViewById(R.id.rating_btn)
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

        // Get user's rating, calculate the new rating and update the database
        ratingButton.setOnClickListener{
            val numStars = ratingBar.rating
            // update rating to the database
            val updateQuery = db.collection("jobs").whereEqualTo("uid", model.uid)
                .whereEqualTo("title", model.title).whereEqualTo("specialistName", model.specialistName)
                .whereEqualTo("description", model.description)

            val newRating = ((model.rating * model.timesRated) + numStars.toDouble()) / (model.timesRated + 1)
            model.timesRated = model.timesRated + 1
            model.rating = newRating

            updateQuery.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        document.reference.set(model)
                    }
                }
            }
            Toast.makeText(this, "Thank you for the rating!", Toast.LENGTH_SHORT).show()

        }


        // Hide the "Message Lister" button for user's own listings
        // Show 'Delete listing' option instead

        if(model.uid == auth.currentUser?.uid){
            messageButton.text = getString(R.string.remove_listing)

            // set different background color
            messageButton.setBackgroundColor(Color.parseColor("#DA1B2B"))

            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.START

            messageButton.layoutParams = params

            messageButton.setOnClickListener{
                val dialog = CustomDialogFragment()
                dialog.show(supportFragmentManager, "my dialog")
            }

            // Hide rating option for user's own listing
            ratingBar.isVisible = false
            ratingButton.isVisible = false

        }else{
            lister = User(model.specialistName, model.uid)

            //Message lister activity
            messageButton.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable(MessageListActivity.NAME_KEY, lister)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    // Delete current listing from the database
    override fun deleteListing() {
        val deleteQuery = db.collection("jobs").whereEqualTo("uid", model.uid)
            .whereEqualTo("title", model.title).whereEqualTo("specialistName", model.specialistName)
            .whereEqualTo("description", model.description)

        deleteQuery.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    document.reference.delete().addOnSuccessListener {
                        Log.d("debug", "Document deleted successfully")
                        finish()
                    }.addOnFailureListener{
                        Log.w("debug", "Error deleting document", it)
                    }
                }
            }
        }
    }

}


