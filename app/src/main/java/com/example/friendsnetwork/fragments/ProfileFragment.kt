package com.example.friendsnetwork.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.friendsnetwork.R
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.adapters.PersonalFeedAdapter
import com.example.friendsnetwork.adapters.onClick
import com.example.friendsnetwork.databinding.FragmentProfileBinding
import com.example.friendsnetwork.viewmodel.UsersViewModel
import com.example.friendsnetwork.viewmodel.viewModelFactory

class ProfileFragment : Fragment(), onClick {
    private var email:String? = null
    private lateinit var viewModel:UsersViewModel
    private lateinit var binding:FragmentProfileBinding
    private lateinit var mAdapter:PersonalFeedAdapter
    private var totalPost:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        observers(view)

        binding.profileEditButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileSetUpActivity)
        }


    }

    private fun observers(view: View) {
        //Current User Observer
        viewModel.mUser.observe(viewLifecycleOwner) {
            if(it!=null){
                binding.userName.text = it.name
                binding.caption.text = it.bio
//              binding.postNumber.text = viewModel.totalPost.value.toString()
                Log.d("User image",it.profilePic.toString())
                Glide.with(view)
                    .load(it.profilePic)
                    .placeholder(R.drawable.profile)
                    .into(binding.profileDp)
            }
        }

        // Current User's Post Observer
        viewModel.mCurrUserPost.observe(viewLifecycleOwner){
            mAdapter.submitList(it)
            totalPost = it.size
            binding.postNumber.text = totalPost.toString()
            Log.d("TotalPosts",totalPost.toString())
        }

    }

    fun init(view: View){
        val mSharePref = this.activity?.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE)
        email = mSharePref?.getString(SHARE_PREF_EMAIL,null)
        viewModel = ViewModelProvider(this,viewModelFactory(email))[UsersViewModel::class.java]
        mAdapter = PersonalFeedAdapter(this)
        binding.personlFeedRecyclerView.adapter = mAdapter
        binding.personlFeedRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        Log.d("heyuser",viewModel.mUser.value.toString())
    }

    override fun onPostClick() {
        findNavController().navigate(R.id.action_profileFragment_to_personalFeedFragment)
    }

}