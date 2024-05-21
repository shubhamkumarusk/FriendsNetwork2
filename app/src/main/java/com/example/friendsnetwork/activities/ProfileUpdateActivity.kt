package com.example.friendsnetwork.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.friendsnetwork.R
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.databinding.ActivityProfileSetUpBinding
import com.example.friendsnetwork.databinding.ActivityProfileUpdateBinding
import com.example.friendsnetwork.fragments.ProfileFragment
import com.example.friendsnetwork.models.UsersModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import com.example.friendsnetwork.viewmodel.UsersViewModel
import com.example.friendsnetwork.viewmodel.viewModelFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUpdateActivity : AppCompatActivity() {
    private val api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)
    private lateinit var viewModel: UsersViewModel
    private var mImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    lateinit var binding: ActivityProfileUpdateBinding
    private lateinit var base64:String
    private var profilePic:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mSharePref = getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE)
        val email = mSharePref.getString(SHARE_PREF_EMAIL,null)
        storageRef = FirebaseStorage.getInstance().reference.child("UserProfile")
        viewModel = ViewModelProvider(this, viewModelFactory(email))[UsersViewModel::class.java]

        viewModel.mUser.observe(this){user->
//            Log.d("shubhamUser",user.toString())
            if(user!=null){
                Glide.with(this)
                    .load(user.profilePic)
                    .placeholder(R.drawable.profile)
                    .into(binding.profilePic)
                binding.userNameProfile.setText(user.name)
                binding.Caption.setText(user.bio)

                profilePic = user.profilePic
            }
        }

        binding.updateDp.setOnClickListener {
            val options = arrayOf<CharSequence>("Camera","Gallery","Remove Profile Pic")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose")
            builder.setItems(options){_,item->
                when{
                    options[item]=="Camera"->{
                        openCamera()
                    }
                    options[item]=="Gallery"->{
                        openGallery()
                    }
                    options[item]=="Remove Profile Pic"->{
                        binding.profilePic.setBackgroundResource(R.drawable.profile)
                    }
                }
            }
            builder.create().show()
        }

        binding.continueProfile.setOnClickListener {
            val name = binding.userNameProfile.text.toString().trim()
            val bio  = binding.Caption.text.toString().trim()

            if(name.isEmpty()){
                binding.userNameProfile.error = "Please Enter your name!"
                return@setOnClickListener
            }
            if(mImageUri!=null){
                storageRef = storageRef.child(email!!)
                val uploadTask = storageRef.putFile(mImageUri!!)
                uploadTask.addOnCompleteListener{uploadTask->
                    if(uploadTask.isSuccessful){
                        storageRef.downloadUrl.addOnSuccessListener {uri->
                            lifecycleScope.launch {
                                profilePic = uri.toString()
                                Log.d("Done",uri.toString())
                                // Once the profilePic is updated, make the API call
                                makeApiCall(email, name, bio)
                            }
                        }
                    }
                }
            } else {
                // If no image is selected, directly make the API call
                makeApiCall(email!!, name, bio)
            }
        }


    }
    private fun makeApiCall(email: String, name: String, bio: String) {
        val map = HashMap<String,String?>()
        map["name"] = name
        map["bio"] = bio
        map["email"] = email
        map["profilePic"] = profilePic
        Log.d("profileImageUri",mImageUri.toString())

        val call = api.setUpYourProfile(map)
        call.enqueue(object : Callback<UsersModel> {
            override fun onResponse(call: Call<UsersModel>, response: Response<UsersModel>) {
                if(response.code()==200){
                    val intent = Intent(this@ProfileUpdateActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if(response.code()==500){
                    Toast.makeText(this@ProfileUpdateActivity,"Internal Problem", Toast.LENGTH_LONG).show()
                } else{
                    Log.d("unexpectedError",response.code().toString())
                    Toast.makeText(this@ProfileUpdateActivity,"${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UsersModel>, t: Throwable) {
                Toast.makeText(this@ProfileUpdateActivity,t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            mImageUri = data.data
            binding.profilePic.setImageURI(mImageUri)
            Log.d("profilePics",mImageUri.toString())
        }
    }


    private fun openCamera() {

        TODO("Not yet implemented")
    }
}