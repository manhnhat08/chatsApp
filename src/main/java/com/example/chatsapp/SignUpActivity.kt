package com.example.chatsapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import com.example.chatsapp.Models.Users
import com.example.chatsapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        gradient animation background
        val linearLayout: LinearLayout = binding.signUpLayout
        val animationDrawable: AnimationDrawable = linearLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(5000)
        animationDrawable.start()

        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setTitle("Creating account")
        progressDialog.setMessage("We're creating your account")

        binding.tvAlreadyHaveAccount.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUserName.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            val passAgain = binding.etPasswordAgain.text.toString()

            if (username.isEmpty()){
                binding.etUserName.error = "Enter your username"
                return@setOnClickListener
            }
            if (email.isEmpty()){
                binding.etEmail.error = "Enter your email"
                return@setOnClickListener
            }
            if (pass.isEmpty()){
                binding.etPassword.error = "Enter your password"
                return@setOnClickListener
            }
            if (passAgain.isEmpty()){
                binding.etPasswordAgain.error = "Enter your password again"
                return@setOnClickListener
            }

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && passAgain.isNotEmpty()){
                if (pass == passAgain){
                    progressDialog.show()
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                        progressDialog.dismiss()
                        if (it.isSuccessful) {
                            Toast.makeText(this, "User created successfully" , Toast.LENGTH_SHORT).show()
                            val userId = it.result.user!!.uid
                            val profilePic = R.drawable.user.toString()
                            val user = Users(username,email,pass,userId, profilePic)
//                            val id:String = it.result.user!!.uid
                            database.reference.child("Users").child(userId).setValue(user)
//                            val intent = Intent(this, SignInActivity::class.java)
//                            startActivity(intent)
                            binding.etUserName.setText("")
                            binding.etEmail.setText("")
                            binding.etPassword.setText("")
                            binding.etPasswordAgain.setText("")
                        } else {
                            Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}