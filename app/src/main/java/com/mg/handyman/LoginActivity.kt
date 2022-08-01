package com.mg.handyman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var googleSignInButton: SignInButton
    private lateinit var signInIntent: Intent
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    companion object{
        private const val RC_GOOGLE_SIGN_IN = 326
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        loginButton = findViewById(R.id.login_btn)
        signupButton = findViewById(R.id.signUp_btn)
        googleSignInButton = findViewById(R.id.google_sign_in_btn)

        loginButton.setOnClickListener{
            val intent = Intent(this, EmailLoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Use Google's default sign-in button graphic
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD)

        // Setup google sign-in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)
        googleSignInButton.setOnClickListener {
            signInIntent = client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if the user is signed in (non-null) and update UI accordingly
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        // TODO -
        if (currentUser == null){
            Log.w(TAG, "User is null, not going to navigate")
            return
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()  // don't return to the login activity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if(requestCode == RC_GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "FirebaseAuthWithGoogle: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch(e: ApiException){
                // Google sign in failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser

                    // Add user to Firestore collection
                    if (user != null){
                        val user1 = hashMapOf(
                            "displayName" to (user.displayName),
                            "uid" to (user.uid),
                        )
                        db.collection("users").add(user1)
                            .addOnSuccessListener { Log.d(SignUpActivity.TAG, "DocumentSnapshot added with ID; ${it.id}") }
                            .addOnFailureListener { Log.w(SignUpActivity.TAG, "Error adding document", it) }
                    }

                    updateUI(user)

                }else{
                    // if sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", it.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
}