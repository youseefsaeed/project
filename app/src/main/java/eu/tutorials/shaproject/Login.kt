package eu.tutorials.shaproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import eu.tutorials.shaproject.RetrofitClient.Companion.getRetrofitObject
import eu.tutorials.shaproject.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.StringBuilder


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val retrofit: Retrofit = getRetrofitObject()
    private val teacherApi = retrofit.create(TeacherApi::class.java)
    private val adminApi = retrofit.create(AdminApi::class.java)
    private val roomApi = retrofit.create(RoomApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            btnSignIn.setOnClickListener {
                val username: String = userNameTextInputEditText.text.toString()
                val password: String = passwordTextInputEditText.text.toString()
                if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(
                        this@Login,
                        "please enter your username and password",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (password.isEmpty()) {
                    Toast.makeText(this@Login, "please enter your password", Toast.LENGTH_SHORT)
                        .show()
                } else if (username.isEmpty()) {
                    Toast.makeText(this@Login, "please enter your username", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    loginCall(username, password)
                }
            }
        }
    }

    private fun loginCall(username: String, password: String) {
        val call2 = adminApi.getAdmins(username, password)
        call2.enqueue(object : Callback<Boolean?> {
            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                if (response.isSuccessful && response.body() == true) {
                    val intent = Intent(this@Login, AddEmployee::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val call3 = roomApi.getRooms(username, password)
                    call3.enqueue(object : Callback<List<examResponse>?> {
                        override fun onResponse(
                            call: Call<List<examResponse>?>,
                            response: Response<List<examResponse>?>
                        ) {
                            if (response.isSuccessful && response.body()!!.isNotEmpty()) {
                                val intent = Intent(this@Login, exam::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                val call = teacherApi.getTeachers(username, password)
                                call.enqueue(object : Callback<List<TeacherResponse>?> {
                                    override fun onResponse(
                                        call: Call<List<TeacherResponse>?>,
                                        response: Response<List<TeacherResponse>?>
                                    ) {
                                        if (response.isSuccessful && response.body()!!
                                                .isNotEmpty()
                                        ) {
                                            val teacher = response.body()!!
                                            val teacher_name = StringBuilder()
                                            val teacher_id = StringBuilder()
                                            for (mydata in teacher) {
                                                teacher_name.append(mydata.name)
                                                teacher_id.append(mydata.teacher_id)
                                            }

                                            val intent =
                                                Intent(this@Login, CourseScreen::class.java)
                                            intent.putExtra(
                                                Constants.teacher_id,
                                                teacher_id.toString().toIntOrNull()
                                            )
                                            intent.putExtra(
                                                Constants.teacher_name,
                                                teacher_name.toString()
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else if (password.isNotEmpty() && username.isNotEmpty()) {
                                            Toast.makeText(
                                                this@Login,
                                                "Wrong enrties",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<List<TeacherResponse>?>,
                                        t: Throwable
                                    ) {

                                        Log.e("Login", "problem in  " + t.message)

                                    }
                                })
                            }
                        }

                        override fun onFailure(call: Call<List<examResponse>?>, t: Throwable) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                Log.e("Login", "problem in  " + t.message)
            }
        })
    }


}










