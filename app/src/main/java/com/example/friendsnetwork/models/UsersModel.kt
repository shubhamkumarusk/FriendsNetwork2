package com.example.friendsnetwork.models

data class UsersModel(
    val _id:String,
    val name:String="",
    val bio:String="",
    val profilePic:String? = null,
    val profileSetup:Boolean = false
)
