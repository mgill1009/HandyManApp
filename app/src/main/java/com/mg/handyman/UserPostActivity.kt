package com.mg.handyman

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * This activity takes user input for posting a new job listing
 * input is validated
 * and the job entry is saved to the firestore db
 */

class UserPostActivity : AppCompatActivity() {
    private lateinit var jobTitle : String
    private lateinit var jobDesc : String
    private lateinit var jobPrice : String
    private lateinit var jobEstTime : String
    private lateinit var phone: String
    private lateinit var location: String
    private lateinit var auth: FirebaseAuth
    private lateinit var images: ArrayList<Int>
    private lateinit var spinner: Spinner
    private lateinit var myAdapter: SpinnerAdapter

    private val db = Firebase.firestore

    companion object{
        const val TAG = "UserPostActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)
        auth = Firebase.auth
        spinner = findViewById(R.id.image_spinner)
        loadImages()

        val usernameTV = findViewById<TextView>(R.id.userName)
        val name = "Posting by ${auth.currentUser?.displayName}"
        usernameTV.text = name

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Reference: https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android#:~:text=getMenuInflater().inflate(R.menu.mymenu%2C%20menu)%3B
        menuInflater.inflate(R.menu.menu_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Reference: https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android#:~:text=activities%0A%40Override-,public%20boolean%20onOptionsItemSelected(MenuItem%20item)%20%7B,-int%20id%20%3D%20item
        // add a new job posting
        if (item.itemId == R.id.postBtn) {
            if(postEntry())
                finish()
        // log the user out
        }else if(item.itemId == R.id.logoutBtn){
            Log.i(TAG, "Logout")
            // Logout the user
            auth.signOut()
            val logOutIntent = Intent(this, LoginActivity::class.java)
            // clear the entire back stack
            logOutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logOutIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Posts a new job entry to the database
     */
    private fun postEntry(): Boolean {
        val jobTitleET = findViewById<TextInputEditText>(R.id.userJobTitle)
        val jobDescET = findViewById<TextInputEditText>(R.id.userJobDesc)
        val jobPriceET = findViewById<TextInputEditText>(R.id.userJobPrice)
        val jobDurationET = findViewById<TextInputEditText>(R.id.userJobEstTime)
        val phoneET = findViewById<TextInputEditText>(R.id.userPhoneNumber)
        val locationET = findViewById<TextInputEditText>(R.id.userLocation)

        jobTitle = jobTitleET.text.toString()
        jobDesc = jobDescET.text.toString()
        jobPrice = jobPriceET.text.toString()
        jobEstTime = jobDurationET.text.toString()
        phone = phoneET.text.toString()
        location = locationET.text.toString()

        if(!validateInputs())
            return false

        val titleArray = ArrayList<String>()
        val t = jobTitle.lowercase()
        for (i in t.indices){
            titleArray.add(t.substring(0, i))
        }

        val createdAt = com.google.firebase.Timestamp(Date())

        // create a new job with provided entries
        val job = hashMapOf(
            "title" to jobTitle,
            "price" to jobPrice.toDouble(),
            "duration" to jobEstTime.toDouble(),
            "specialistName" to auth.currentUser?.displayName.toString(),
            "description" to jobDesc,
            "location" to location,
            "phone" to phone.toLong(),
            "pictureId" to spinner.selectedItem,
            "rating" to 0,
            "timesRated" to 0,
            "uid" to (auth.currentUser?.uid ?: ""),
            "titleAsArray" to titleArray,
            "createdAt" to createdAt
        )

        // Add a new job entry to Firestore with a generated ID
        db.collection("jobs")
            .add(job)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID; ${it.id}")
            }
            .addOnFailureListener{
                Log.w(TAG, "Error adding document", it)
            }

        return true
    }

    /**
     * Validates all entered inputs and returns true if passed
     */
    private fun validateInputs(): Boolean {
        if(jobTitle.trim().isEmpty()){
            Toast.makeText(this, "Please enter a job title", Toast.LENGTH_SHORT).show()
            return false
        }
        if(jobPrice.trim().isEmpty()){
            Toast.makeText(this, "Please enter the price", Toast.LENGTH_SHORT).show()
            return false
        }

        if(jobEstTime.trim().isEmpty()){
            Toast.makeText(this, "Please enter the time estimate", Toast.LENGTH_SHORT).show()
            return false
        }
        if(phone.trim().isEmpty()){
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if(!android.util.Patterns.PHONE.matcher(phone).matches()){
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if(location.trim().isEmpty()){
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    /**
     * placeholder images for job types
     * used in spinner
     */
    private fun loadImages() {
        images = ArrayList()
        images.add(R.drawable.job)
        images.add(R.drawable.carpenter)
        images.add(R.drawable.electrician)
        images.add(R.drawable.electrician2)
        images.add(R.drawable.lawn_mowing)
        images.add(R.drawable.nanny)
        images.add(R.drawable.nanny2)
        images.add(R.drawable.plumber)
        images.add(R.drawable.plumber2)
        images.add(R.drawable.plumber3)
        images.add(R.drawable.truck_driver)
        images.add(R.drawable.truck_driver_2)
        images.add(R.drawable.truck_driver_3)

        myAdapter = SpinnerAdapter(this, images)
        spinner.adapter = myAdapter
    }
}