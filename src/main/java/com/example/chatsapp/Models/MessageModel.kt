package com.example.chatsapp.Models

class MessageModel {
    var uId:String = ""
    var message:String = ""
    var timestamp:Long = 0
    var messageId:String = ""

    constructor(uId:String, message: String, timestamp: Long, messageId:String){
        this.uId = uId
        this.message = message
        this.timestamp = timestamp
        this.messageId = messageId
    }

    constructor(){ }

    constructor(uId: String, message: String){
        this.uId = uId
        this.message = message
    }
}