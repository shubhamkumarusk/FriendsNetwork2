package com.example.friendsnetwork.models

import java.io.Serializable

data class UserLoginModel(
    val email:String,
    val password:String
):Serializable
