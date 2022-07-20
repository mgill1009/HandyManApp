package com.mg.handyman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun logInClicked(view: View) {
        // TODO: authentication and error handling
        val intent = Intent(this, MainActivity::class.java).apply{

        }
        startActivity(intent)
    }
}