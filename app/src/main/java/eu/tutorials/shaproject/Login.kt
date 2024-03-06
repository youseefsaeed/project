package eu.tutorials.shaproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.StringBuilder
import kotlin.collections.ArrayList


class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val logging = HttpLoggingInterceptor()
        Log.e("Login","failaerin"+logging)
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        val teacherApi = retrofit.create(TeacherApi::class.java)
        val adminApi = retrofit.create(AdminApi::class.java)
        val roomApi = retrofit.create(RoomApi::class.java)
        rectangle_1.setOnClickListener {
            val username:String=enter_your_.text.toString()
            val password:String=enter_your_1.text.toString()
            if(enter_your_.text.toString().isEmpty()&&enter_your_1.text.toString().isEmpty()){
                Toast.makeText(this, "please enter your username and password", Toast.LENGTH_SHORT).show()
            }
            else if(enter_your_1.text.toString().isEmpty()){
                Toast.makeText(this@Login, "please enter your password", Toast.LENGTH_SHORT).show()
            }
            else if(enter_your_.text.toString().isEmpty()){
                Toast.makeText(this@Login, "please enter your username", Toast.LENGTH_SHORT).show()
            }
            val call2=adminApi.getAdmins(username,password)
            call2.enqueue(object : Callback<Boolean?> {
                override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                    if (response.isSuccessful &&response.body()==true){
                        val intent = Intent(this@Login, addemployee::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val call3=roomApi.getRooms(username,password)
                        call3.enqueue(object : Callback<Boolean?> {
                            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                                if (response.isSuccessful &&response.body()==true){
                                    val intent = Intent(this@Login, exam::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    val call = teacherApi.getTeachers(username, password)
                                    call.enqueue(object : Callback<List<TeacherResponse>?> {
                                        override fun onResponse(call: Call<List<TeacherResponse>?>, response: Response<List<TeacherResponse>?>) {
                                            if (response.isSuccessful&& response.body()!!.isNotEmpty() ) {
                                                val teacher = response.body()!!
                                                val teacher_name=StringBuilder()
                                                val teacher_id=StringBuilder()
                                                for (mydata in teacher){
                                                    teacher_name.append(mydata.name)
                                                    teacher_id.append(mydata.teacher_id)
                                                }

                                                val intent = Intent(this@Login, coursescreen::class.java)
                                                intent.putExtra(Constants.teacher_id,teacher_id.toString().toIntOrNull())
                                                intent.putExtra(Constants.teacher_name,teacher_name.toString())
                                                startActivity(intent)
                                                finish()
                                            } else if (enter_your_1.text.toString().isNotEmpty()&& enter_your_.text.toString().isNotEmpty()) {
                                                Toast.makeText(this@Login, "Wrong enrties", Toast.LENGTH_SHORT).show()
                                            }
                                            else{}
                                        }

                                        override fun onFailure(call: Call<List<TeacherResponse>?>, t: Throwable) {

                                            Log.e("Login", "problem in  " + t.message)

                                        }
                                    })
                                }
                            }

                            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                    Log.e("Login", "problem in  " + t.message)
                }
            })





        }}}










