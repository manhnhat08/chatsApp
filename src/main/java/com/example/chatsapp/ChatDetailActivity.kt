package com.example.chatsapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatsapp.Adapters.ChatAdapter
import com.example.chatsapp.Models.MessageModel
import com.example.chatsapp.databinding.ActivityChatDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

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





        binding.send.setOnClickListener {

            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formattedTime = currentTime.format(formatter)
            Log.d("TAG11", formattedTime)
//            LocalDateTime.now().atZone(ZoneId.of("GMT+7")).toEpochSecond()
            val message: String = binding.etMessage.text.toString()
            val model: MessageModel = MessageModel(senderID!!, message)
            model.timestamp = Date().time
            binding.etMessage.setText("")
//            Log.d("TAG", Date().time.toString())
//            val currentTimeMillis = System.currentTimeMillis()
//            val currentDate = Date(currentTimeMillis)
//            Log.d("TAG1", currentDate.toString())


            database.reference.child("chats").child(senderRoom).push().setValue(model)
                .addOnSuccessListener {
                    database.reference.child("chats").child(receiverRoom).push().setValue(model)
                        .addOnSuccessListener {

                        }
                }



        }


    }
}