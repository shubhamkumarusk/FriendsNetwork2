package com.example.friendsnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.adapters.FeedAdapter
import com.example.friendsnetwork.adapters.onClickHandel
import com.example.friendsnetwork.databinding.FragmentPersonalFeedBinding
import com.example.friendsnetwork.models.PostsModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import com.example.friendsnetwork.viewmodel.UsersViewModel
import com.example.friendsnetwork.viewmodel.viewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PersonalFeedFragment : Fragment(), onClickHandel {
    private lateinit var binding: FragmentPersonalFeedBinding
    private lateinit var mAdapter: FeedAdapter
    private var email: String? = null
    private val api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)
    private val viewmodel: UsersViewModel by activityViewModels{
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
        binding = FragmentPersonalFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val mSharePref = this.activity?.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE)
        email = mSharePref?.getString(SHARE_PREF_EMAIL,null)
        mAdapter = FeedAdapter(this,email!!)

        binding.personalRecyclerView.adapter = mAdapter
        binding.personalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewmodel.mCurrUserPost.observe(viewLifecycleOwner){
            mAdapter.submitList(it)
        }
    }

    override fun onLikeButtonClick(post: PostsModel){
        val map = HashMap<String,String>()
        map["email"] = email!!
        api.likePost(post.postId,map).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>){
                if(response.code()==200){
                    val updatedLikes = post.likes?.toMutableList()
                    updatedLikes?.add(email!!)
                    val updatedPost = post.copy(likes = updatedLikes)
                    viewmodel.updatePost(updatedPost)
                    mAdapter.notifyDataSetChanged()
                    Log.d("Liked",response.body().toString())
                }
                else if(response.code()==201)
                {
                    val updatedLikes = post.likes?.toMutableList()
                    updatedLikes?.remove(email!!)
                    val updatedPost = post.copy(likes = updatedLikes)
                    viewmodel.updatePost(updatedPost)
                    mAdapter.notifyDataSetChanged()
                    Log.d("DisLiked",response.body().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("Likederror",t.message.toString())
            }

        })
    }

    override fun onCommentButtonClick(post: PostsModel) {

    }


}