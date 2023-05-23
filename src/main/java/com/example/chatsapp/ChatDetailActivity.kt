package com.example.chatsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatsapp.Adapters.ChatAdapter
import com.example.chatsapp.Models.MessageModel
import com.example.chatsapp.databinding.ActivityChatDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Objects

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var imagePickerActivityResult: ActivityResultLauncher<Intent>
    private lateinit var storage: FirebaseStorage

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        val senderID = auth.uid
        val recieveID = intent.getStringExtra("userID")
        val username = intent.getStringExtra("username")
        val profilepic = intent.getStringExtra("profilepic")

        binding.tvUserNameChat.text = username
        Picasso.get().load(profilepic).placeholder(R.drawable.user).into(binding.profileImage)

        binding.backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val messageModels: ArrayList<MessageModel> = ArrayList()
        val chatAdapter = ChatAdapter(messageModels, this, recieveID!!)
        binding.chatRecyclerView.adapter = chatAdapter

        val layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = layoutManager

        val senderRoom: String = senderID + recieveID
        val receiverRoom: String = recieveID + senderID

        database.reference.child("chats").child(senderRoom)
            .addValueEventListener(object : ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageModels.clear()
                    for (dataSnapshot in snapshot.children){
                        val model: MessageModel? = dataSnapshot.getValue(MessageModel::class.java)
                        model!!.messageId = dataSnapshot.key!!
                        messageModels.add(model)
                    }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("TAG", "Failed to read value.", error.toException());
                }
        })


        binding.ivFileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 33, null)
            imagePickerActivityResult.launch(intent)
        }



        binding.send.setOnClickListener {
            val date = Date()
            val message: String = binding.etMessage.text.toString()
            val model = MessageModel(senderID!!, message)

                binding.etMessage.setText("")
                database.reference.child("chats").child(senderRoom).push().setValue(model)
                    .addOnSuccessListener {
                        database.reference.child("chats").child(receiverRoom).push().setValue(model)
                            .addOnSuccessListener {
                            }
                    }

//            val lastMsgObj = HashMap<String, Any>()
//            lastMsgObj.put("lastMsg", model.message)
//            lastMsgObj["lastMsgTime"] = date.time
//            database.reference.child("chats").child(senderRoom).updateChildren(lastMsgObj)
//            database.reference.child("chats").child(receiverRoom).updateChildren(lastMsgObj)

        }
        imagePickerActivityResult =

            registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->

                if (result != null) {
                    // getting URI of selected Image
                    val sFile: Uri? = result.data?.data
                    binding.profileImage.setImageURI(sFile)
                    val reference: StorageReference = storage.reference.child("send_image_msg")
                        .child(FirebaseAuth.getInstance().uid!!)

                    if (sFile != null) {
                        reference.putFile(sFile).addOnSuccessListener {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val message: String = uri.toString()
                                val model = MessageModel(senderID!!, message)
//                                database.reference.child("chats").child(FirebaseAuth.getInstance().uid!!).child("send_image_msg").setValue(model)
                                database.reference.child("chats").child(senderRoom).push().setValue(model)
                                    .addOnSuccessListener {
                                        database.reference.child("chats").child(receiverRoom).push().setValue(model)
                                            .addOnSuccessListener {
                                            }
                                    }
                                Toast.makeText(this, "Send picture Uploaded", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }


    }
}