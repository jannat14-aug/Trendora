package com.example.trendora

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)

                val credential =
                    GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {

                            Toast.makeText(
                                this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val currentUser = FirebaseAuth.getInstance().currentUser

                            if (currentUser != null) {

                                val database = FirebaseDatabase.getInstance(
                                    "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app"
                                ).reference

                                val userMap = HashMap<String, Any>()

                                userMap["uid"] = currentUser.uid
                                userMap["username"] = currentUser.displayName ?: ""
                                userMap["email"] = currentUser.email ?: ""
                                userMap["profileImage"] = currentUser.photoUrl?.toString() ?: ""

                                database.child("Users")
                                    .child(currentUser.uid)
                                    .setValue(userMap)
                            }
                            startActivity(
                                Intent(
                                    this,
                                    HomeActivity::class.java
                                )
                            )
                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)

        btnGoogle.setOnClickListener {

            val options =
                GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                    .requestIdToken("435841005235-gku9e1njgos5a41p503mai3eak61rjsl.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

            val client = GoogleSignIn.getClient(this, options)

            launcher.launch(client.signInIntent)
        }
    }
    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}