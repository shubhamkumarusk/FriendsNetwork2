package com.example.friendsnetwork.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.friendsnetwork.SHARE_PREF_EMAIL
import com.example.friendsnetwork.SHARE_PREF_NAME
import com.example.friendsnetwork.databinding.ActivityLoginBinding
import com.example.friendsnetwork.models.UserLoginModel
import com.example.friendsnetwork.retrofit.RetrofitHelper
import com.example.friendsnetwork.retrofit.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var email:String? = null
    private val api = RetrofitHelper.getInstance().create(RetrofitServices::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mSharePref = getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE)
        val mEditor = mSharePref.edit()
        email = mSharePref.getString(SHARE_PREF_EMAIL,null)
        Log.d("auth",email.toString())
        if(email!=null){
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.alreadySignin.setOnClickListener{
            val intent = Intent(this,SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.verificationBtn.setOnClickListener {
            val email = binding.emailIdEt.text.toString().trim()
            val password = binding.setPasswordEt.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEt.text.toString().trim()
            if (email.isEmpty()) {
                binding.emailIdEt.error = "Enter Your e-mail"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.setPasswordEt.error = "Enter Your Password"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                binding.confirmPasswordEt.error = "Confirm Your Password"
                return@setOnClickListener
            }
            if(password!=confirmPassword){
                binding.confirmPasswordEt.error = "Enter the same Password"
                return@setOnClickListener
            }
            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                val map = HashMap<String,String>()
                map["email"] = email
                map["password"] = password
                map["confirmPassword"] = confirmPassword
                val call = api.registerUser(map)
                call.enqueue(object :Callback<UserLoginModel>{
                    override fun onResponse(call: Call<UserLoginModel>, response: Response<UserLoginModel>) {
                        if(response.code()==200){
                            mEditor.apply {
                                putString(SHARE_PREF_EMAIL,email)
                                apply()
                            }
                            if(it!=null){
                                val intent = Intent(this@LoginActivity,ProfileSetUpActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        else if(response.code()==400){
                            binding.emailIdEt.error = "Already Registered!!"
                        }
                    }

                    override fun onFailure(call: Call<UserLoginModel>, t: Throwable) {
                        showToast(t.message.toString())
                    }

                })
            }


        }

        
    }
    fun showToast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }
}