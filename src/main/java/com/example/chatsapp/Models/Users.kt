package com.example.chatsapp.Models



class Users{

    var profilepic :String = ""
    var username :String = ""
    var email :String = ""
    var password :String = ""
    var userID :String = ""
    var lastMessage :String = ""
    var status :String = ""

//  constructor
    constructor(profilepic :String, username: String, email: String, password: String, userID :String, lastMessage :String, status :String) {
        this.profilepic = profilepic
        this.username = username
        this.email = email
        this.password = password
        this.userID = userID
        this.lastMessage = lastMessage
        this.status = status
}

    constructor(){}

//  SignUp Constructor
    constructor(username: String, email: String, password: String, userID: String, profilepic :String) {
        this.username = username
        this.email = email
        this.password = password
        this.userID = userID
        this.profilepic = profilepic
}
}
