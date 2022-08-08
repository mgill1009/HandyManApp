package com.mg.handyman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * This activity lets the user login if they
 * already have an account - using their email and password
 * authenticates with Firebase
 */

class EmailLoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView

    companion object{
        const val TAG = "EmailLoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        auth = Firebase.auth

        loginButton = findViewById(R.id.login_btn)

        loginButton.setOnClickListener{
            val email = findViewById<EditText>(R.id.email_editText).text
            val password = findViewById<EditText>(R.id.password_editText).text

            if(!SignUpActivity.isValidEmail(email.toString())){
                Toast.makeText(this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show()
            }else{
                // Send to Firebase Auth
                auth.signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener(this){
                        if(it.isSuccessful){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            updateUI(user)
                        }else{
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithEmail:failure", it.exception)
                            Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
            }
        }

        signUpTextView = findViewById(R.id.signUp_tv)
        signUpTextView.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if the user is signed in and update UI accordingly
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
}