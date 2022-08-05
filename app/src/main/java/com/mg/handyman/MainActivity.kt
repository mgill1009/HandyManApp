package com.mg.handyman

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
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
    private val query = db.collection("jobs")
    private lateinit var query2: Query
    private lateinit var searchQuery: Query
    private var allListings = true
    private var searched = false

    private lateinit var addJobButton: Button

    companion object{
        private const val TAG = "MainActivity"
        const val SELECTED_JOB = "Selected job"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_title_1)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        Log.d("debug", "inside onCreate")

        welcomeTextView = findViewById(R.id.welcome_tv)
        rvJobs = findViewById(R.id.rv_jobs)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        // display logged in user's name
        val name = currentUser?.displayName
        var message = ""
        if (name != null) {
            for(word in name.split(" ")){
                message += word[0]
            }
        }
        Log.d("debug", "Message is $message")

        welcomeTextView.text = message

        // Query all jobs from the database in a background thread and populate recyclerView
        val query = runBlocking { db.collection("jobs")  }
        populateJobs(query)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("debug", "Inside OnNewIntent")
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent){
        Log.d("debug", "inside handleIntent")
        if(Intent.ACTION_SEARCH == intent.action){
            intent.getStringExtra(SearchManager.QUERY).also { query ->
                doMySearch(query)
            }
        }
    }

    private fun doMySearch(queryMessage: String?) {
        Log.d("debug", "inside doMySearch")
        // TODO call populate jobs with app query
        searched = queryMessage != ""

        searchQuery = db.collection("jobs").whereEqualTo("title", queryMessage)
        populateJobs(searchQuery)
        Log.d("debug", "Search query is $queryMessage")
    }

    /** Retrieve the collection of jobs from Firestore and populate the
     * RecyclerView
     */
    private fun populateJobs(query: Query) {
        Log.d("debug", "inside populateJobs")
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

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = (menu?.findItem(R.id.menu_search)?.actionView as SearchView)
        searchView.apply {
            maxWidth = 750
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = false
        }


        val textChangeListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(cs: String): Boolean {
                    if (TextUtils.isEmpty(cs)) {
                        //Text is cleared, do your thing
                        Log.d("debug", "Search box is cleared")
                        searched = false

                        if(allListings)
                            populateJobs(query)
                        else
                            populateJobs(query2)

                    }
                    return false
                }
            }

        searchView.setOnQueryTextListener(textChangeListener)

        return true
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
            if(allListings){
                // now we want to show only mylistings
                item.title = getString(R.string.all_listings)
                query2 = db.collection("jobs").whereEqualTo("uid", auth.currentUser?.uid)
                populateJobs(query2)
                allListings = false
            }else{
                // now we want to show all listings
                item.title = getString(R.string.my_listings)
                populateJobs(query)
                allListings = true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if(searched)
            populateJobs(searchQuery)
        else{
            if(allListings)
                populateJobs(query)
            else
                populateJobs(query2)
        }
    }
}