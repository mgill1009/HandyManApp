package com.mg.handyman

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserPostActivity : AppCompatActivity() {
    private lateinit var jobTitle : String
    private lateinit var jobDesc : String
    private lateinit var jobPrice : String
    private lateinit var jobEstTime : String
    private lateinit var auth: FirebaseAuth

    companion object{
        const val TAG = "UserPostActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)
        auth = Firebase.auth
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Reference: https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android#:~:text=getMenuInflater().inflate(R.menu.mymenu%2C%20menu)%3B
        menuInflater.inflate(R.menu.menu_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Reference: https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android#:~:text=activities%0A%40Override-,public%20boolean%20onOptionsItemSelected(MenuItem%20item)%20%7B,-int%20id%20%3D%20item
        if (item.itemId == R.id.postBtn) {
            // TODO: add posting functionality
            finish()
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
}