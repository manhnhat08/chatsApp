package com.example.chatsapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatsapp.Adapters.UsersAdapter
import com.example.chatsapp.Models.Users
import com.example.chatsapp.databinding.FragmentStatusBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding
    private lateinit var list: ArrayList<Users>
    private lateinit var database: FirebaseDatabase


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater, container, false)

        list = arrayListOf<Users>()
        database = FirebaseDatabase.getInstance()
        val adapter = UsersAdapter(list, context)
        binding.statusRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
        binding.statusRecyclerView.layoutManager = layoutManager

        database.reference.child("Users").addValueEventListener(object : ValueEventListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                // Lấy dữ liệu mới nhất từ Firebase Realtime Database để xử lý
                for (dataSnapshot in snapshot.children) {
                    val users: Users? = dataSnapshot.getValue(Users::class.java)
                    users!!.userID = dataSnapshot.key!!
                    list.add(users)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "Failed to read value.", databaseError.toException())
            }
        })
        return binding.root
    }
}