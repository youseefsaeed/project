package eu.tutorials.shaproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.android.synthetic.main.activity_coursescreen.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

class coursescreen : AppCompatActivity() {
    lateinit var Adapter_coursesscreen:Adapter_coursesscreen
    lateinit var linearLayoutManager: LinearLayoutManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coursescreen)
        var doctor_name=intent.getStringExtra(Constants.teacher_name)
        var doctor_id = intent.getIntExtra(Constants.teacher_id, 0)
        welcome_doctor.text="Welcome,$doctor_name."

        recycleview.setHasFixedSize(true)
        linearLayoutManager=LinearLayoutManager(this)
        recycleview.layoutManager=linearLayoutManager

        val logging2 = HttpLoggingInterceptor()
        Log.i("coursescreen","failaerin"+logging2)
        logging2.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient2 = OkHttpClient.Builder()
        httpClient2.addInterceptor(logging2)

        val retrofit2: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient2.build())
            .build()
        val courseApi = retrofit2.create(CoursesApi::class.java)
        val call = courseApi.getTeachers(doctor_id)
        call.enqueue(object : Callback<List<CoursesX>?> {
            override fun onResponse(call: Call<List<CoursesX>?>, response: Response<List<CoursesX>?>) {
                if (response.isSuccessful ) {
                    val courses = response.body()!!

                    Adapter_coursesscreen= Adapter_coursesscreen(baseContext,courses)
                    Adapter_coursesscreen.courseClickListener = object : Adapter_coursesscreen.CourseClickListener {
                        override fun onCourseClicked(courseId: Int,coursename:String) {

                            val intent = Intent(this@coursescreen, course_options::class.java)
                            intent.putExtra(Constants.course_id,courseId)
                            intent.putExtra(Constants.course_name,coursename)
                            intent.putExtra(Constants.teacher_name,doctor_name)
                            startActivity(intent)

                        }
                    }
                    Adapter_coursesscreen.notifyDataSetChanged()
                    recycleview.adapter=Adapter_coursesscreen

                }
                else{}
            }

            override fun onFailure(call: Call<List<CoursesX>?>, t: Throwable) {

                Log.e("coursescreen", "problem in  " + t.message)

            }
        })
        btn_logout.setOnClickListener {
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }


    }
}