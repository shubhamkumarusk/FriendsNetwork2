package com.example.friendsnetwork.models

import com.google.gson.annotations.SerializedName

data class PostsModel(
    @SerializedName("_id")
    val postId:String,
    val image:String?=null,
    val caption:String?=null,
    val likes:MutableList<String>?=null,
    var comments:MutableList<CommentModel>?=null,
    val user:UsersModel
)