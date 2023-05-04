package com.example.chatsapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.example.chatsapp.Models.Users
import com.example.chatsapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private companion object {
        private const val TAG = "SignInActivity"
        private const val RC_GOOGLE_SIGN_IN: Int = 65
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        gradient animation background
        val linearLayout:LinearLayout = binding.signInLayout
        val animationDrawable: AnimationDrawable = linearLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(5000)
        animationDrawable.start()

        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this@SignInActivity)
        progressDialog.setTitle("Login")
        progressDialog.setMessage("Login to your account")

        //        Configure google sign in
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.tvClickForSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isEmpty()){
                binding.etEmail.error = "Enter your email"
                return@setOnClickListener
            }
            if (pass.isEmpty()){
                binding.etPassword.error = "Enter your password"
                return@setOnClickListener
            }

            if (email.isNotEmpty() && pass.isNotEmpty()){
                progressDialog.show()
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                    progressDialog.dismiss()
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "You entered wrong!!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            SignIn()
        }

        if (auth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun SignIn(){
        val signInIntent:Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Log.d("TAG","firebaseAuthWithGoogle :"+ account.id)
                firebaseAuthWithGoogleAccount(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("TAG", "signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() {
                if (it.isSuccessful){
                    Log.d("TAG", "SignInWithCredential: success")
                    val user: FirebaseUser? = auth.currentUser
                    val users: Users = Users()
                    users.userID = user!!.uid
                    users.email = user.email.toString()
                    users.username = user.displayName.toString()
                    users.profilepic = user.photoUrl.toString()

                    database.reference.child("Users").child(user.uid).setValue(users)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Sign in with "+ users.email, Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("TAG", "SignInWithCredential: failure", it.exception)
                }
            }
    }
}