package com.example.friendsnetwork.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.friendsnetwork.R
import com.example.friendsnetwork.databinding.PostsListBinding
import com.example.friendsnetwork.models.PostsModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import kotlin.math.absoluteValue

class FeedAdapter(private val mlistner:onClickHandel,val email:String):ListAdapter<PostsModel,FeedAdapter.FeedViewHolder>(DiffCallBack){

    companion object{
        val DiffCallBack = object: DiffUtil.ItemCallback<PostsModel>(){

            override fun areItemsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
                return oldItem.postId==newItem.postId
            }

            override fun areContentsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
                return oldItem==newItem && oldItem.likes?.size == newItem.likes?.size
            }

        }
    }

    class FeedViewHolder(private val binding:PostsListBinding,val listner:onClickHandel,val email:String):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(posts: PostsModel){
            if(posts.user!=null){
                binding.username.text = posts.user.name
                Glide.with(itemView.context)
                    .load(posts.user.profilePic)
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(binding.dpImage)
            }
            binding.captionFeed.text = posts.caption
            binding.likeNum.text = posts.likes?.size.toString()

            binding.likeBtn.setOnClickListener {
                listner.onLikeButtonClick(posts)
            }
            binding.commentBtn.setOnClickListener {
                listner.onCommentButtonClick(posts)
            }

            Glide.with(itemView.context)
                .load(posts.image)
                .centerCrop()
                .placeholder(R.drawable.profile)
                .into(binding.imageFeed)

            val liked = posts.likes?.contains(email)
            if(liked==true){
                binding.likeBtn.setImageDrawable(
                    ContextCompat.getDrawable(binding.likeBtn.context,
                        R.drawable.like_btn))

            }
            else{
                binding.likeBtn.setImageDrawable(ContextCompat.getDrawable(binding.likeBtn.context,R.drawable.liked_btn_outlined))
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            PostsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mlistner,email
        )
    }


    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val curr = getItem(position)
        holder.bind(curr)
    }
}

interface onClickHandel{
    fun onLikeButtonClick(post:PostsModel)
    fun onCommentButtonClick(post:PostsModel)
}