package com.mg.handyman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpButton: Button
    private lateinit var signInTextView: TextView
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.firestore

    companion object{
        const val TAG = "SignUpActivity"

        // referenced: https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth

        signUpButton = findViewById(R.id.signUp_btn)
        signUpButton.setOnClickListener{

            val email = findViewById<EditText>(R.id.email_editText).text
            val password = findViewById<EditText>(R.id.password_editText).text
            val confirmPass = findViewById<EditText>(R.id.confirmPass_editText).text
            val firstName = findViewById<EditText>(R.id.firstName_editText).text
            val lastName = findViewById<EditText>(R.id.lastName_editText).text

            Log.d(TAG, "Email is ${email.toString()}")

            if(!isValidEmail(email.toString())){
                Toast.makeText(this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.email_editText).setText("")
            } else if (password.toString() != confirmPass.toString()){
                findViewById<EditText>(R.id.password_editText).setText("")
                findViewById<EditText>(R.id.confirmPass_editText).setText("")
                Toast.makeText(this, "Passwords didn't match! Please try again.",
                    Toast.LENGTH_SHORT).show()
            } else if(password.toString().length < 6) {
            //If password is less has a length less than 6
            Toast.makeText(this, "Password must have a length of 6.",
                Toast.LENGTH_SHORT).show()
            } else {
                // Create a new account in Firebase

                auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener(this){ it ->
                        if(it.isSuccessful){
                            // Sign in success, update UI with the signed in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = it.result.user
                            if (user != null) {
                                Log.d(TAG, "USer is ${user.uid}")
                            }

                            // Update user's name - profile pic (later)
                            val profileUpdates = userProfileChangeRequest {
                                this.displayName = "$firstName $lastName"
                            }

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener{task ->
                                    if(task.isSuccessful){
                                        Log.d(TAG, "User profile updated")

                                        val name = "$firstName $lastName"
                                        val user1 = hashMapOf(
                                            "displayName" to name,
                                            "uid" to user.uid,
                                        )

                                        db.collection("users").add(user1)
                                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot added with ID; ${it.id}") }
                                            .addOnFailureListener{
                                                Log.w(TAG, "Error adding document", it)
                                            }
                                        updateUI(user)
                                    }
                                }

                        }else{
                            // if sign in fails, display a message to the user
                            Log.w(TAG, "createUserWithEmail:failure", it.exception)
                            Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
            }
        }
        signInTextView = findViewById(R.id.signIn_tv)
        signInTextView.setOnClickListener{
            val intent = Intent(this, EmailLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            // TODO
            // no access
        }else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        // check if the user is signed in (non- null) and update UI accordingly
        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}