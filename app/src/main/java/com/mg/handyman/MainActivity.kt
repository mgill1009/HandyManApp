package com.mg.handyman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class JobViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

/**
 * A list of all jobs will be displayed in this activity
 * Only logged in users can view this screen
 */

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var welcomeTextView: TextView
    private lateinit var rvJobs: RecyclerView

    private val decimalFormat = DecimalFormat("0.#")

    private val db = Firebase.firestore

    // This button is for testing purpose only and will be used in UserPostActivity later
    private lateinit var addJobButton: Button

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        welcomeTextView = findViewById(R.id.welcome_tv)
        rvJobs = findViewById(R.id.rv_jobs)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        // display logged in user's name
        val message = "Hi ${currentUser?.displayName}"
        welcomeTextView.text = message

        populateJobs()

        // TODO need to move this to UserPostActivity later
        addJobButton = findViewById(R.id.addJob_btn)
        addJobButton.setOnClickListener{
            addJobEntry()
        }

    }

    /** Retrieve the collection currently posted jobs from Firestore and populate the
     * RecyclerView
     */
    private fun populateJobs() {
        val query = db.collection("jobs")

        val options = FirestoreRecyclerOptions.Builder<Job>().setQuery(query, Job::class.java)
            .setLifecycleOwner(this).build()

        // create an adapter for recyclerView
        val adapter = object: FirestoreRecyclerAdapter<Job, JobViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
                // Can create a custom layout later, if time permits
                val view = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.job_list_item, parent, false)
                return JobViewHolder(view)
            }

            override fun onBindViewHolder(holder: JobViewHolder, position: Int, model: Job) {
                val nameTV: TextView = holder.itemView.findViewById(R.id.name_textView)
                val jobTitleTV: TextView = holder.itemView.findViewById(R.id.jobTitleListing_textView)
                val priceTV: TextView = holder.itemView.findViewById(R.id.price_textView)
                val durationTV: TextView = holder.itemView.findViewById(R.id.duration_textView)
                val locationTV: TextView = holder.itemView.findViewById(R.id.location_textView)

                nameTV.text = model.specialistName
                jobTitleTV.text = model.title
                val price = "$ ${decimalFormat.format(model.price)}"
                priceTV.text = price
                val duration = "${decimalFormat.format(model.duration)} hrs"
                durationTV.text = duration
                locationTV.text = model.location
            }
        }
        rvJobs.adapter = adapter
        // set layout manager on recyclerView
        rvJobs.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logoutBtn){
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

    // TODO (remove this later, once implemented in UserPost) Test function to add job entry to Firestore
    private fun addJobEntry(){

        // create a new job with provided entries
        val job = hashMapOf(
            "title" to "Plumber",
            "price" to 50,
            "duration" to 60,
            "specialistName" to "John Doe",
            "description" to "",
            "location" to "Burnaby, BC"
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

    }
}