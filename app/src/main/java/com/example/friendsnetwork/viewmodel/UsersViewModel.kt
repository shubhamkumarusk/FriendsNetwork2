package com.example.friendsnetwork.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.friendsnetwork.models.PostsModel
import com.example.friendsnetwork.models.UsersModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersViewModel(private val mEmail:String?):ViewModel(){
    private val _mUser = MutableLiveData<UsersModel>()
    val mUser: LiveData<UsersModel> = _mUser

    private val _mAllPosts = MutableLiveData<MutableList<PostsModel>>()
    val mAllPosts:LiveData<MutableList<PostsModel>> = _mAllPosts

    private val  _mcurrUserPost = MutableLiveData<MutableList<PostsModel>>()
    val mCurrUserPost:LiveData<MutableList<PostsModel>> = _mcurrUserPost

    private val _totalPost = MutableLiveData<Int>()
    var totalPost:LiveData<Int> = _totalPost
    private val api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)
    init {
        if(mEmail!=null){
            fetchUserByEmail(mEmail){
                _mUser.postValue(it)
            }
            getAUserPost(mEmail)
        }
        getAllPosts()

    }
    fun fetchUserByEmail(email: String, callback:(UsersModel?)->Unit){
        api.getUserByEmail(email).enqueue(object : retrofit2.Callback<UsersModel> {
            override fun onResponse(call: retrofit2.Call<UsersModel>, response: retrofit2.Response<UsersModel>) {
                if (response.code()==200) {
                    response.body()?.let {
                        Log.d("viewmodelUser",it.toString())
                        callback(it)
                    }
                }
                else{
                    callback(null)
                    Log.d("viewmodelUser","chud gya")
                }
            }

            override fun onFailure(call: retrofit2.Call<UsersModel>, t: Throwable) {
                callback(null)
                Log.d("viewmodelUser",t.message.toString())
            }
        })
    }

    fun getAllPosts() = viewModelScope.launch(Dispatchers.IO) {
        _mAllPosts.postValue(api.getAllPosts())
    }

    fun updatePost(post: PostsModel) {
        val posts = _mAllPosts.value ?: mutableListOf()
        val index = posts.indexOfFirst {it.postId == post.postId}
        if (index != -1) {
             posts[index] = post
            _mAllPosts.postValue(posts)
        }

        val personalposts = _mcurrUserPost.value ?: mutableListOf()
        val personalindex = personalposts.indexOfFirst {it.postId == post.postId}
        if (personalindex != -1) {
            personalposts[personalindex] = post
            _mcurrUserPost.postValue(personalposts)
        }
    }

    fun getAUserPost(email:String) = viewModelScope.launch(Dispatchers.IO) {
        _mcurrUserPost.postValue(api.getAUserPost(email))
    }






}

class viewModelFactory(val email: String?):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(email) as T
        }
        return super.create(modelClass)
    }
}
