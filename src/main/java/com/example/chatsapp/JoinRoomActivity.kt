package com.example.chatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatsapp.databinding.ActivityJoinRoomBinding

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enterBtn.setOnClickListener {
            val pass = binding.passJoinRoom.text.toString()

            if (pass == "1234"){
                val intent = Intent(this, GroupChatActivity::class.java)
                startActivity(intent)
            }
        }
    }
}