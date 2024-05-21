package com.example.friendsnetwork.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.friendsnetwork.R
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.adapters.CommentsAdapter
import com.example.friendsnetwork.adapters.FeedAdapter
import com.example.friendsnetwork.adapters.onClickHandel
import com.example.friendsnetwork.databinding.FragmentHomeBinding
import com.example.friendsnetwork.models.CommentModel
import com.example.friendsnetwork.models.PostsModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import com.example.friendsnetwork.viewmodel.UsersViewModel
import com.example.friendsnetwork.viewmodel.viewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), onClickHandel {
    private lateinit var binding:FragmentHomeBinding
    private var email: String? = null
    private lateinit var mAdapter: FeedAdapter
    private val api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)
    private val viewmodel:UsersViewModel by activityViewModels{
        viewModelFactory(email)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =  FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

    }
    fun init(view: View){
        val mSharePref = this.activity?.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE)
        email = mSharePref?.getString(SHARE_PREF_EMAIL,null)
        mAdapter = FeedAdapter(this,email!!)
        binding.postRecyclerView.adapter = mAdapter
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewmodel.mAllPosts.observe(viewLifecycleOwner){
            mAdapter.submitList(it)
            Log.d("Posts",it.toString())
        }
    }

    override fun onLikeButtonClick(post: PostsModel) {
        val currentLikes = post.likes?.toMutableList() ?: mutableListOf()

        val hasLiked = currentLikes.contains(email)
        if (hasLiked) {
            currentLikes.remove(email)
        } else {
            currentLikes.add(email!!)
        }

        val updatedPost = post.copy(likes = currentLikes)
        viewmodel.updatePost(updatedPost)
        mAdapter.notifyDataSetChanged() // Immediate UI update

        val map = HashMap<String, String>()
        map["email"] = email!!
        api.likePost(post.postId, map).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200 || response.code() == 201) {
                    // Backend updated successfully
                    Log.d("Like/Dislike", response.body().toString())
                } else {
                    // Revert the like/unlike action if the backend fails
                    if(hasLiked){
                        currentLikes.add(email!!)
                    } else {
                        currentLikes.remove(email)
                    }
                    val revertedPost = post.copy(likes = currentLikes)
                    viewmodel.updatePost(revertedPost)
                    mAdapter.notifyDataSetChanged()
                    Log.d("Like/Dislike Error", response.code().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Revert the like/unlike action if the backend fails
                if(hasLiked){
                    currentLikes.add(email!!)
                } else {
                    currentLikes.remove(email)
                }
                val revertedPost = post.copy(likes = currentLikes)
                viewmodel.updatePost(revertedPost)
                mAdapter.notifyDataSetChanged()
                Log.d("Like/Dislike Failure", t.message.toString())
            }
        })
    }

    override fun onCommentButtonClick(post: PostsModel) {
        showDialog(post)
    }
    private fun showDialog(post: PostsModel){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.comment_box)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.comment_recyclerView)
        val commentEditText = dialog.findViewById<EditText>(R.id.comment_etv)
        val sendButton = dialog.findViewById<ImageView>(R.id.send_button)
        val commentAdapter = CommentsAdapter()

        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentAdapter.submitList(post.comments)
        Log.d("posts Comment",post.comments.toString())

        sendButton.setOnClickListener {
            val comment = commentEditText.text.toString()
            val map = HashMap<String,String>()
            map["email"] = email!!
            map["text"] = comment
            api.addCommentToPost(post.postId,map).enqueue(object :Callback<CommentModel>{
                override fun onResponse(
                    call: Call<CommentModel>,
                    response: Response<CommentModel>
                ) {
                    val newComment = response.body()
                    val updatedComment = post.comments?.toMutableList()
                    updatedComment?.add(newComment!!)
                    post.comments = updatedComment
                    commentAdapter.submitList(updatedComment)
                    commentAdapter.notifyDataSetChanged()
                    commentEditText.text = null
                }

                override fun onFailure(call: Call<CommentModel>, t: Throwable) {
                    Toast.makeText(requireContext(),"${t.message}",Toast.LENGTH_LONG).show()
                }

            })
        }
        dialog.show()
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
    }


}