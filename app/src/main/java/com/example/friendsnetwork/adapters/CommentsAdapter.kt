package com.example.friendsnetwork.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.friendsnetwork.databinding.CommentListBinding
import com.example.friendsnetwork.databinding.FragmentPersonalFeedBinding
import com.example.friendsnetwork.models.CommentModel
import com.example.friendsnetwork.models.PostsModel

class CommentsAdapter:ListAdapter<CommentModel,CommentsAdapter.CommentsViewHolder>(DiffCallBack){

    companion object{
        val DiffCallBack = object :DiffUtil.ItemCallback<CommentModel>(){

            override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
               return  oldItem._id ==newItem._id
            }


            override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
                return oldItem==newItem
            }

        }
    }
    class CommentsViewHolder(val binding: CommentListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(comment:CommentModel){
            binding.comment.text = comment.comment
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            CommentListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }


    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
       val curr = getItem(position)
        holder.bind(curr)
    }
}
