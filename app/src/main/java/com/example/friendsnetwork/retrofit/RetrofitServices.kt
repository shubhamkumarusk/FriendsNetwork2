package com.example.friendsnetwork.retrofit

import com.example.friendsnetwork.models.PostsModel
import com.example.friendsnetwork.models.UserLoginModel
import com.example.friendsnetwork.models.UsersModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitServices {
    @POST("/register")
    fun registerUser(@Body map:HashMap<String,String>) : Call<UserLoginModel>

    @POST("/signin")
    fun signinUser(@Body map:HashMap<String,String>):Call<Void>

    @POST("/profile")
    fun setUpYourProfile(@Body map:HashMap<String,String?>):Call<UsersModel>

    @GET("/profile/{email}")
    fun getUserByEmail(@Path("email") email:String): Call<UsersModel>

    @POST("/{id}/likes")
    fun likePost(@Path("id") id:String,@Body map:HashMap<String,String>):Call<String>

    @GET("/allposts")
    suspend fun getAllPosts():ArrayList<PostsModel>

    @GET("/{email}/posts")
    suspend fun getAUserPost(@Path("email") email: String):ArrayList<PostsModel>

    @POST("/postpics")
    fun PostFeed(@Body map:HashMap<String,String>):Call<Void>

    @POST("/{id}/comment")
    fun addCommentToPost(@Path("id") id:String,@Body map:HashMap<String,String>):Call<Void>

}