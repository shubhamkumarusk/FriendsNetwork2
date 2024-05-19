package com.example.friendsnetwork.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.friendsnetwork.R
import com.example.friendsnetwork.databinding.PersonalFeedBinding
import com.example.friendsnetwork.models.PostsModel

class PersonalFeedAdapter(private val mListner:onClick):ListAdapter<PostsModel,PersonalFeedAdapter.PersonalFeedViewHolder>(DiffCallBack) {

    class PersonalFeedViewHolder(private val binding: PersonalFeedBinding,val listner:onClick):RecyclerView.ViewHolder(binding.root){
        fun bind(post:PostsModel){
            try{
                Glide.with(itemView.context)
                    .load(post.image)
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .into(binding.feedImage)
            } catch (e:Exception){
                Log.d("FeedImage",e.message.toString())
            }

            itemView.setOnClickListener {
                listner.onPostClick()
            }

        }
    }
    companion object{
        val DiffCallBack = object :DiffUtil.ItemCallback<PostsModel>(){

            override fun areItemsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
               return oldItem.postId==newItem.postId
            }


            override fun areContentsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalFeedViewHolder {
       return PersonalFeedViewHolder(
           PersonalFeedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
           ,mListner
       )
    }


    override fun onBindViewHolder(holder: PersonalFeedViewHolder, position: Int) {
        val curr = getItem(position)
        holder.bind(curr)
    }
}
interface onClick{
    fun onPostClick()
}