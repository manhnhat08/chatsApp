package com.example.chatsapp.Adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsapp.Models.MessageModel
import com.example.chatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {

    val messageModels:ArrayList<MessageModel>
    val context: Context
    var recId: String = ""
    val SENDER_VIEW_TYPE:Int = 1
    val RECEIVER_VIEW_TYPE:Int = 2

    constructor(messageModel: ArrayList<MessageModel>, context: Context?){
        this.messageModels = messageModel
        this.context = context!!
    }

    constructor(messageModel: ArrayList<MessageModel>, context: Context?, recId: String){
        this.messageModels = messageModel
        this.context = context!!
        this.recId = recId
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SENDER_VIEW_TYPE){
            val view: View = LayoutInflater.from(context).inflate(R.layout.show_sender, parent, false)
            return SenderViewHolder(view)
        }
        else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.show_reciever, parent, false)
            return ReceiverViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
       if (messageModels[position].uId == FirebaseAuth.getInstance().uid){
           return SENDER_VIEW_TYPE
       }
        else {
            return RECEIVER_VIEW_TYPE
       }
    }

    override fun getItemCount(): Int {
        return messageModels.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageModel: MessageModel = messageModels[position]
//        Delete message in chat application
        holder.itemView.setOnLongClickListener{
            AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this message")
                .setPositiveButton("yes") { dialog, which ->
                    val database = FirebaseDatabase.getInstance()
                    val senderRoom: String = FirebaseAuth.getInstance().uid + recId
                    database.reference.child("chats").child(senderRoom)
                        .child(messageModel.messageId).setValue(null)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
//            true == return
             true
        }

        if (holder.javaClass == SenderViewHolder::class.java){
//            Ép kiểu đổi holder thành senderViewHolder
            (holder as SenderViewHolder).senderMsg.text = messageModel.message
            val imageUrl = messageModel.message as? String
            if (imageUrl!!.startsWith("http")){
                (holder as SenderViewHolder).senderMsg.visibility = View.GONE
            }
            Picasso.get()
                .load(imageUrl)
                .into((holder as SenderViewHolder).senderImg)
//            (holder).senderImg.layoutParams.width = 500
//            (holder).senderImg.layoutParams.height = 700
        }
        if (holder.javaClass == ReceiverViewHolder::class.java) {
            (holder as ReceiverViewHolder).receiverMsg.text = messageModel.message
            val imageUrl = messageModel.message as? String
            if (imageUrl!!.startsWith("http")){
                (holder).receiverMsg.visibility = View.GONE
            }
            Picasso.get()
                .load(imageUrl)
                .into((holder).receiverImg)
//            (holder).receiverImg.layoutParams.width = 500
//            (holder).receiverImg.layoutParams.height = 700

        }
    }


    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMsg: TextView = itemView.findViewById(R.id.tvReceiverText)
        val receiverImg: ImageView = itemView.findViewById(R.id.msg_receive_img)
        val receiverTime: TextView = itemView.findViewById(R.id.tvReceiverTime)
    }

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMsg: TextView = itemView.findViewById(R.id.tvSenderText)
        val senderImg: ImageView = itemView.findViewById(R.id.msg_sender_img)
        val senderTime: TextView = itemView.findViewById(R.id.tvSenderTime)
    }
}