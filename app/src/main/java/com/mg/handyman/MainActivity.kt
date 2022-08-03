package com.mg.handyman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
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

    private lateinit var addJobButton: Button

    companion object{
        private const val TAG = "MainActivity"
        const val SELECTED_JOB = "Selected job"
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
    }

    /** Retrieve the collection currently posted jobs from Firestore and populate the
     * RecyclerView
     */
    private fun populateJobs() {

        val query = runBlocking { db.collection("jobs")  }

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
                val imageView: ImageView = holder.itemView.findViewById(R.id.jobImage_imageView)
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
                imageView.setImageResource(model.pictureId)

                // Opens selected job in a new activity
                holder.itemView.setOnClickListener{
                    val intent = Intent(it.context, JobActivity::class.java)
                    intent.putExtra(SELECTED_JOB, model)
                    it.context.startActivity(intent)
                }
            }
        }

        rvJobs.adapter = adapter
        // set layout manager on recyclerView
        val mLayoutManager = LinearLayoutManager(this)
        rvJobs.layoutManager = mLayoutManager
        val mDividerItemDecoration = DividerItemDecoration(
            rvJobs.context,
            mLayoutManager.orientation
        )
        rvJobs.addItemDecoration(mDividerItemDecoration)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addJobBtn) {
            val intent = Intent(this, UserPostActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.logoutBtn) {
            Log.i(TAG, "Logout")
            // Logout the user
            auth.signOut()
            val logOutIntent = Intent(this, LoginActivity::class.java)
            // clear the entire back stack
            logOutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logOutIntent)
        } else if (item.itemId == R.id.messagesBtn) {
            val intent = Intent(this, MessageListActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.myListingsBtn){
            // TODO
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        populateJobs()
    }
}