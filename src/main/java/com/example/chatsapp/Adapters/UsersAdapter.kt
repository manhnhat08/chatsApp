package com.example.chatsapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsapp.ChatDetailActivity
import com.example.chatsapp.Models.MessageModel
import com.example.chatsapp.Models.Users
import com.example.chatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UsersAdapter: RecyclerView.Adapter<UsersAdapter.ViewHolder> {

     private val list:ArrayList<Users>
     val context: Context

    constructor(list: ArrayList<Users>, context: Context?){
        this.list = list
        this.context = context!!
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.show_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users: Users = list[position]
        Picasso.get().load(users.profilepic).placeholder(R.drawable.user).into(holder.image)
        holder.userName.text = users.username

        FirebaseDatabase.getInstance().reference.child("chats")
            .child(FirebaseAuth.getInstance().uid + users.userID)
            .orderByChild("timestamp")
            .limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()){
                        for (dataSnapshot in snapshot.children){
                            holder.lastMessage.text = dataSnapshot.child("message").value.toString()


                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("TAG", "Failed to read value.", error.toException());
                }
            })


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatDetailActivity::class.java)
            intent.putExtra("userID", users.userID)
            intent.putExtra("profilepic", users.profilepic)
            intent.putExtra("username", users.username)
            context.startActivity(intent)


        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.profile_image)
        val userName: TextView = itemView.findViewById(R.id.userNameList)
        val lastMessage: TextView =  itemView.findViewById(R.id.lastMessage)
    }



}