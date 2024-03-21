package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_coursescreen.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CourseScreen : AppCompatActivity() {
    private lateinit var adapterCoursesScreen: AdapterCoursesScreen
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val retrofit: Retrofit = getRetrofitObject()
    private val courseApi = retrofit.create(CoursesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coursescreen)

        var doctorName = intent.getStringExtra(Constants.teacher_name)
        var doctorId = intent.getIntExtra(Constants.teacher_id, 0)

        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("doctor_id", doctorId)
            .apply()

        welcome_doctor.text = "Welcome,$doctorName."

        setupRecyclerView()
        fetchDataFromApi(doctorId, doctorName!!)

        btn_logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun fetchDataFromApi(doctorId: Int, doctorName: String) {
        val call = courseApi.getTeachers(doctorId)
        call.enqueue(object : Callback<List<CoursesX>?> {
            override fun onResponse(
                call: Call<List<CoursesX>?>,
                response: Response<List<CoursesX>?>
            ) {
                if (response.isSuccessful) {
                    val courses = response.body()!!

                    adapterCoursesScreen = AdapterCoursesScreen(baseContext, courses)
                    adapterCoursesScreen.courseClickListener =
                        object : AdapterCoursesScreen.CourseClickListener {
                            override fun onCourseClicked(courseId: Int, coursename: String) {

                                val intent = Intent(this@CourseScreen, course_options::class.java)
                                intent.putExtra(Constants.course_id, courseId)
                                intent.putExtra(Constants.course_name, coursename)
                                intent.putExtra(Constants.teacher_name, doctorName)
                                startActivity(intent)

                            }
                        }
                    adapterCoursesScreen.notifyDataSetChanged()
                    recycleview.adapter = adapterCoursesScreen

                } else {
                }
            }

            override fun onFailure(call: Call<List<CoursesX>?>, t: Throwable) {

                Log.e("coursescreen", "problem in  " + t.message)

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

    private fun setupRecyclerView() {
        recycleview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recycleview.layoutManager = linearLayoutManager
        adapterCoursesScreen = AdapterCoursesScreen(baseContext, emptyList())

    }
}