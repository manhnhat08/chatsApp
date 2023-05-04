package com.example.chatsapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatsapp.Models.MessageModel
import com.example.chatsapp.Models.Users
import com.example.chatsapp.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnSave.setOnClickListener {
            val  status: String = binding.etStatus.text.toString()
            val username: String = binding.etUserNameProfile.text.toString()

            val obj = HashMap<String, Any>()
            obj["username"] = username
            obj["status"] = status

            database.reference.child("Users").child(FirebaseAuth.getInstance().uid!!)
                .updateChildren(obj)
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
        }



        database.reference.child("Users").child(FirebaseAuth.getInstance().uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                  val users = snapshot.getValue(Users::class.java)

                    Picasso.get().load(users!!.profilepic).placeholder(R.drawable.user).into(binding.profileImage)

                    binding.etStatus.setText(users.status)
                    binding.etUserNameProfile.setText(users.username)

                }

                override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("TAG", "Failed to read value.", error.toException());
                }
            })


//        Upload profile_image into DB storage firebase
        binding.plus.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 33)

        }

        fun onActivityResult(requestCode: Int,resultCode: Int, data: Intent) {
            super.onActivityResult(requestCode, resultCode, data)
            if (data.data != null) {

                val sFile: Uri = data.data!!
                binding.profileImage.setImageURI(sFile)


                val reference: StorageReference = storage.reference.child("profile_pictures")
                    .child(FirebaseAuth.getInstance().uid!!)

                reference.putFile(sFile).addOnSuccessListener {
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        val userRef = database.reference.child("Users").child(FirebaseAuth.getInstance().uid!!)
                            userRef.child("profilepic").setValue(uri.toString())
                        Toast.makeText(this, "Profile picture Uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}