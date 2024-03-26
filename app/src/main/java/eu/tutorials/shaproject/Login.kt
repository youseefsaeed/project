package eu.tutorials.shaproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import eu.tutorials.shaproject.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_coursescreen.*
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import java.lang.StringBuilder


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val retrofit: Retrofit = getRetrofitObject()
    private val teacherApi = retrofit.create(TeacherApi::class.java)
    private val adminApi = retrofit.create(AdminApi::class.java)
    private val roomApi = retrofit.create(RoomApi::class.java)
    private val examapi=retrofit.create(ExamApi::class.java)
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
                            }  else {
                                val call3 = roomApi.getRooms(username, password)
                                call3.enqueue(object : Callback<List<examResponse>?> {
                                    override fun onResponse(
                                        call: Call<List<examResponse>?>,
                                        response: Response<List<examResponse>?>
                                    ) {
                                        if (response.isSuccessful  && response.body()!!.isNotEmpty()) {
                                            val exams = response.body()!!
                                            val exam_id = StringBuilder()
                                            for (mydata in exams) {
                                                exam_id.append(mydata.room_id)
                                            }
                                            val call4=examapi.getExams(exam_id.toString().toInt())
                                            call4.enqueue(object : Callback<List<ExamDetails>?> {
                                                override fun onResponse(call: Call<List<ExamDetails>?>, response: Response<List<ExamDetails>?>) {
                                                    if (response.isSuccessful) {
                                                        val exams_details = response.body()!!
                                                        val exam_id=StringBuilder()
                                                        val exam_date=StringBuilder()
                                                        val exam_time=StringBuilder()
                                                        val exam_pass=StringBuilder()
                                                        val name=StringBuilder()
                                                        for (mydata in exams_details) {
                                                            exam_id.append(mydata.exam_id)
                                                            exam_date.append(mydata.exam_date)
                                                             exam_time.append(mydata.exam_time)
                                                             exam_pass.append(mydata.exam_pass)
                                                             name.append(mydata.name)
                                                        }
                                                        val intent = Intent(this@Login, exam::class.java)
                                                        intent.putExtra(Constants.exam_id,exam_id.toString())
                                                        intent.putExtra(Constants.exam_date,exam_date.toString())
                                                        intent.putExtra(Constants.exam_time,exam_time.toString())
                                                        intent.putExtra(Constants.exam_pass,exam_pass.toString())
                                                        intent.putExtra(Constants.exam_name,name.toString())
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        val intent = Intent(this@Login, no_exam::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }

                                                override fun onFailure(call: Call<List<ExamDetails>?>, t: Throwable) {

                                                    Log.e("Login", "problem in  " + t.message)

                                                }
                                            })
                                        }
                                        else if (password.isNotEmpty() && username.isNotEmpty()) {
                                            Toast.makeText(
                                                this@Login,
                                                "Wrong enrties",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else{

                                        }

                                    }

                                    override fun onFailure(call: Call<List<examResponse>?>, t: Throwable) {
                                        TODO("Not yet implemented")
                                    }
                                })

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

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                Log.e("Login", "problem in  " + t.message)
            }
        })

    }

    private fun getRetrofitObject(): Retrofit {
        val logging = HttpLoggingInterceptor()
        val httpClient = OkHttpClient.Builder()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }
}

















