package com.example.friendsnetwork.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.databinding.ActivitySigninBinding
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import com.example.friendsnetwork.viewmodel.UsersViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {
    private val api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mSharePref = getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE)
        val mEditor = mSharePref.edit()
        binding.signinBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            val map = HashMap<String,String>()
            map["email"] = email
            map["password"] = password
            if (email.isEmpty()) {
                binding.emailEt.error = "Enter Your e-mail"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.passwordEt.error = "Enter your password"
                return@setOnClickListener
            }
            if(email.isNotEmpty() && password.isNotEmpty()){
                val call = api.signinUser(map)
                call.enqueue(object :Callback<Void>{

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.code()==200){
                            mEditor.apply {
                                mEditor.apply {
                                    putString(SHARE_PREF_EMAIL,email)
                                    apply()
                                }
                            }
                            val intent = Intent(this@SigninActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if(response.code()==400){
                            binding.passwordEt.error = "Please enter correct password!"
                        }
                        else if(response.code()==404){
                            Toast.makeText(this@SigninActivity,"You're not Registered",Toast.LENGTH_LONG).show()
                        }
                    }


                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("error",t.message.toString())
                        Toast.makeText(this@SigninActivity,"Error: ${t.message}",Toast.LENGTH_LONG).show()

                    }

                })
            }

        }
        binding.notRegistered.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}