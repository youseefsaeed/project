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
import java.util.*

class course_options : AppCompatActivity() {
    private val sharedPreferences3 by lazy { getSharedPreferences("my_prefs3", Context.MODE_PRIVATE) }
    private val examId by lazy { sharedPreferences3.getString("exam_id", "")!!.toIntOrNull() }
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

        if (!isClickedToday(courseId) ) {
            sharedPreferences.edit().putInt("manuallycounterforcourse$courseId", 0).apply()
            sharedPreferences.edit().putInt("nfccounterforcourse$courseId", 0).apply()
            Constants.studentsforfile.clear()
            Constants.students_idsforcheck.clear()
            sharedPreferences.edit().putStringSet("students_idsforcheck${courseId}", emptySet<String>()).apply()
        }
        if (!isClickedToday(examId!!)) {
            sharedPreferences.edit().putInt("manuallycounterforexam$examId", 0).apply()
            sharedPreferences.edit().putInt("nfccounterforexam$examId", 0).apply()
            Constants.studentsforfile.clear()
            Constants.students_idsforcheck.clear()
            sharedPreferences.edit().putStringSet("students_idsforcheckforexam${examId}",emptySet<String>()).apply()

        }
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
                    apiResponseList?.let { apiList ->
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val database = AppDatabase.getInstance(applicationContext)
                                val localStudents = database.studentDao().getAllStudents()

                                // Delete students from local database that are not present in the API response
                                localStudents.forEach { localStudent ->
                                    if (apiList.none { apiStudent ->
                                            apiStudent.students.student_id == localStudent.studentId
                                        }) {
                                        database.studentDao().delete(localStudent)
                                        Log.d("Coroutine", "Deleted student: ${localStudent.name}")
                                    }
                                }

                                // Insert or update students from API response
                                apiList.forEach { apiResponse ->
                                    val studentEntity = StudentEntity(
                                        name = apiResponse.students.name,
                                        grade = apiResponse.students.grade,
                                        faculty = apiResponse.students.faculty,
                                        studentId = apiResponse.students.student_id,
                                        percentage = apiResponse.students.percentage,
                                        lectures = apiResponse.students.lectures
                                    )
                                    database.studentDao().insertOrUpdate(studentEntity)
                                    Log.d("Coroutine", "Data inserted or updated successfully")
                                }
                            } catch (e: Exception) {
                                Log.e("Coroutine", "Error updating data: ${e.message}")
                            }
                        }
                    }
                } else {
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
    private fun isClickedToday(courseid: Int): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)

        // Get the last clicked day for the given course ID
        val sharedPreferences = getSharedPreferences("ButtonClick_$courseid", MODE_PRIVATE)
        val lastClickedDay = sharedPreferences.getInt("lastClickedDay", -1)

        if (lastClickedDay != today) {
            // Update the last clicked day for the given course ID
            sharedPreferences.edit().putInt("lastClickedDay", today).apply()
            return false
        }

        return true
    }
    override fun onBackPressed() {
        val intent = Intent(this, CourseScreen::class.java)
        startActivity(intent)
        finish()
    }
}