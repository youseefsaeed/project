package eu.tutorials.shaproject

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_coursescreen.*
import kotlinx.android.synthetic.main.activity_exam.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*

class exam : AppCompatActivity() {
    private val retrofit: Retrofit = RetrofitClient.getRetrofitObject()
    private val ExamApi = retrofit.create(pass_e::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        setupLogout()

        val sharedPreferences3 = getSharedPreferences("my_prefs3", Context.MODE_PRIVATE)
        val courseid = sharedPreferences3.getInt("course_id", 0)
        val sharedPreferences2 = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        sharedPreferences2.edit()
            .putInt("code", 5)
            .putInt("course_id", courseid)
            .apply()

        val sharedPreferences = getSharedPreferences("my_prefs3", Context.MODE_PRIVATE)
        val examPassword = sharedPreferences.getString("exam_pass", "") ?: ""
        val examName = sharedPreferences.getString("name", "") ?: ""

        showExamDetails(
            examName = sharedPreferences.getString("name", ""),
            examTime = sharedPreferences.getString("exam_time", ""),
            examDate = sharedPreferences.getString("exam_date", ""),
            examId = sharedPreferences.getString("exam_id", "")
        )

        rectangle_4.setOnClickListener {
            val enteredPassword = enter_your_1.text.toString()
            if (enteredPassword == examPassword) {
                getExam1(examName, enteredPassword)
            } else {
                showWrongPasswordError()
            }
        }
    }

    private fun setupLogout() {
        rectangle_7.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showExamDetails(examName: String?, examTime: String?, examDate: String?, examId: String?) {
        exam_name_e.text = "Exam name: $examName"
        time_10_00_.text = "Time: $examTime"
        date_dd_mm_.text = "Date: $examDate"
        exam_name_e1.text = "Exam ID: $examId"
        exam_invigi.text = "Exam invigilator: Ahmed"
    }

    private fun getExam1(examName: String?, enteredPassword: String) {
        val call = ExamApi.getExam1(examName!!, enter_your_1.text.toString()!!)
        call.enqueue(object : Callback<List<examResponse2>?> {
            override fun onResponse(call: Call<List<examResponse2>?>, response: Response<List<examResponse2>?>) {
                if (response.isSuccessful) {
                    showExamAttendanceScreen()
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<List<examResponse2>?>, t: Throwable) {
                Log.e("exam", "problem in  " + t.message)
            }
        })
    }

    private fun showWrongPasswordError() {
        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
    }

    private fun showExamAttendanceScreen() {
        val intent = Intent(this, take_atten::class.java)
        startActivity(intent)
    }
}