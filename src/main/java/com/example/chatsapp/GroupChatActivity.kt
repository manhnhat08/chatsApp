package com.example.chatsapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatsapp.Adapters.ChatAdapter
import com.example.chatsapp.Models.MessageModel
import com.example.chatsapp.databinding.ActivityGroupChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var messageModels: ArrayList<MessageModel>
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        binding.backArrow.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

        database = FirebaseDatabase.getInstance()
        messageModels = ArrayList()

        val senderId = FirebaseAuth.getInstance().uid
        binding.tvUserNameChat.setText("Friends Group")

        adapter = ChatAdapter(messageModels, this)
        binding.chatRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = layoutManager

//        Get DB in Group chat
        database.reference.child("Group Chat")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageModels.clear()
                    for (dataSnapshot in snapshot.children){
                        val model: MessageModel? = dataSnapshot.getValue(MessageModel::class.java)
                        messageModels.add(model!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("TAG", "Failed to read value.", error.toException());
                }
            })

        binding.send.setOnClickListener {
            val message = binding.etMessage.text.toString()
            val model = MessageModel(senderId!!, message)
            model.timestamp = Date().time
            binding.etMessage.setText("")
            database.reference.child("Group Chat").push().setValue(model)
                .addOnSuccessListener {

                }
        }
    }
}