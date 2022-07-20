package com.mg.handyman

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class UserPostActivity : AppCompatActivity() {
    private lateinit var jobTitle : String
    private lateinit var jobDesc : String
    private lateinit var jobPrice : String
    private lateinit var jobEstTime : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)
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
        }
        return super.onOptionsItemSelected(item)
    }
}