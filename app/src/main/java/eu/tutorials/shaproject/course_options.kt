package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import eu.tutorials.shaproject.db.AppDatabase
import eu.tutorials.shaproject.db.StudentEntity
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class course_options : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_options)

        setupViews()
        setupListeners()

    }

    private fun setupViews() {
        val sharedPreferences = this.getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        val courseHomepage = sharedPreferences.getString("course_name","")
        course_homepage.text = "$courseHomepage Home Page"

        val username = sharedPreferences.getString("doctorName","")
        username2.text = username

        val courseId = sharedPreferences.getInt("course_id",0)
        course_id_1.text = "Course Id: $courseId"


        fetchStudentData(courseId)
    }
    private fun setupListeners() {
        logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        val sharedPreferences = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("code", 1)
            .apply()
        take_attend.setOnClickListener {

            val intent = Intent(this, take_atten::class.java)
            startActivity(intent)
        }

        lectures_hi.setOnClickListener {
            val intent = Intent(this, lecuters_history::class.java)
            startActivity(intent)
        }

        students_me.setOnClickListener {
            val intent = Intent(this, students_metrics::class.java)
            startActivity(intent)
        }
    }
    private fun fetchStudentData(courseId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .client(RetrofitClient.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(create_lecuture::class.java)
        val call3 = apiService.getStudentOfCourse(courseId)
        call3.enqueue(object : Callback<List<ApiResponseformetrics>> {
            override fun onResponse(call: Call<List<ApiResponseformetrics>>, response: Response<List<ApiResponseformetrics>>) {
                if (response.isSuccessful) {
                    val apiResponseList = response.body()
                    apiResponseList?.forEach { apiResponse ->
                        val studentEntity = StudentEntity(
                            name = apiResponse.students.name,
                            grade = apiResponse.students.grade,
                            faculty = apiResponse.students.faculty,
                            studentId = apiResponse.students.student_id,
                            percentage = apiResponse.students.percentage,
                            lectures = apiResponse.students.lectures
                        )
                        // Insert the studentEntity into the Room database using the DAO
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val database = AppDatabase.getInstance(applicationContext)
                                database.studentDao().insert(studentEntity)
                                Log.d("Coroutine", "Data inserted successfully")
                            } catch (e: Exception) {
                                Log.e("Coroutine", "Error inserting data: ${e.message}")
                            }
                        }
                    }
                }

                else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<List<ApiResponseformetrics>>, t: Throwable) {
                Toast.makeText(
                    this@course_options,
                    "Request failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("API_CALL_ERROR", "Error occurred during API call", t)
            }
        })
    }
}