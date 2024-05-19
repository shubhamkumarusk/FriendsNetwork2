package com.example.friendsnetwork.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.friendsnetwork.R
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.databinding.FragmentUploadBinding
import com.example.friendsnetwork.models.PostsModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import com.example.friendsnetwork.viewmodel.UsersViewModel
import com.example.friendsnetwork.viewmodel.viewModelFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UploadFragment : Fragment() {
    private lateinit var binding:FragmentUploadBinding
    private lateinit var api:RetrofitServices
    private lateinit var viewModel:UsersViewModel
    private  var memail:String?=null
    private lateinit var storageRef:StorageReference
    private var feedImage:String? = null
    private var mImageUri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        uploadActivity()
    }

    private fun uploadActivity() {
        binding.gallery.setOnClickListener {
            openGallery()
        }
        binding.uploadBtn.setOnClickListener {
            uploadFeed()
        }
    }

    private fun uploadFeed() {
        val currTime = System.currentTimeMillis().toString()
        storageRef = storageRef.child(currTime)

        if(mImageUri!=null){
            val uploadTask = storageRef.putFile(mImageUri!!)
            uploadTask.addOnCompleteListener{upload->
                if(upload.isSuccessful){
                    storageRef.downloadUrl.addOnSuccessListener {uri->
                        feedImage = uri.toString()
                        makeApiCall(memail!!)

                    }
                }

            }
        }
    }

    private fun makeApiCall(email:String){
        val map = HashMap<String,String>()
        val caption = binding.captionEt.text.toString()
        map["email"] = email
        map["image"] = feedImage!!
        map["caption"] = caption
        api.PostFeed(map).enqueue(object :Callback<Void>{
            
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code()==200){
                    findNavController().navigate(R.id.action_uploadFragment_to_homeFragment)
                }
            }


            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("errorOnfeed",t.message.toString())
                Toast.makeText(requireContext(),"${t.message}",Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==2 && resultCode==Activity.RESULT_OK && data!=null ){
            mImageUri = data.data
            binding.image.setImageURI(mImageUri)
        }
    }

    private fun init(view: View) {
        val mSharePref = this.activity?.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE)
        memail = mSharePref?.getString(SHARE_PREF_EMAIL,null)
        viewModel = ViewModelProvider(this,viewModelFactory(memail))[UsersViewModel::class.java]

        api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)
        storageRef = FirebaseStorage.getInstance().reference.child("UploadFeed")
    }


}