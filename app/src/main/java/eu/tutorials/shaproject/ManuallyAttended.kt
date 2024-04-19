package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import android.widget.Toast
import eu.tutorials.shaproject.Constants.students
import eu.tutorials.shaproject.Constants.students_ids
import eu.tutorials.shaproject.RetrofitClient.Companion.getRetrofitObject
import eu.tutorials.shaproject.db.AppDatabase
import kotlinx.android.synthetic.main.activity_manually_atten.*
import kotlinx.android.synthetic.main.activity_students_metrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.util.*

class ManuallyAttended : AppCompatActivity() {

    private var isBackButtonEnabled = true
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPreferences2 by lazy { getSharedPreferences("my_prefs2", Context.MODE_PRIVATE) }
    private val sharedPreferences3 by lazy { getSharedPreferences("my_prefs3", Context.MODE_PRIVATE) }
    private val examId by lazy { sharedPreferences3.getString("exam_id", "")!!.toIntOrNull() }
    private val courseid by lazy { sharedPreferences2.getInt("course_id", 0) }

    private val code by lazy { sharedPreferences2.getInt("code", 0) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_atten)

        // Initialize sharedPreferences
        sharedPreferences = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)

        setListeners()
        if (code==5){
            updateCounterTextforexam()
        }
        else{
            updateCounterText()
        }

    }

    private fun setListeners() {
        attend.setOnClickListener {
            val studentId = student_id.text.toString()
            if (studentId.isEmpty()) {
                Toast.makeText(
                    this@ManuallyAttended,
                    "Please, write the student id",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            try {
                val studentIdInt = studentId.toInt()
                if (students_ids.contains(studentIdInt) && studentId.isNotEmpty()) {
                    Toast.makeText(
                        this@ManuallyAttended,
                        "Student ID $studentId is already added.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    checkid(studentIdInt)
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(
                    this@ManuallyAttended,
                    "Please enter a valid student ID.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkid(studentId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val database = AppDatabase.getInstance(applicationContext)
                // Fetch the student from the database
                val studentDao = database.studentDao().getStudentById(studentId)

                runOnUiThread {
                    if (studentDao != null) {
                        val studentName = studentDao.name
                        val studentData = "$studentId, $studentName"
                        students.add(studentData)
                        students_ids.add(studentId)
                        if (code==5){
                            val currentCounter = sharedPreferences.getInt("manuallycounterforexam$examId", 0) + 1
                            sharedPreferences.edit().putInt("manuallycounterforexam$examId", currentCounter).apply()

                            updateCounterTextforexam()
                        }
                        else{
                            val currentCounter = sharedPreferences.getInt("manuallycounterforcourse$courseid", 0) + 1
                            sharedPreferences.edit().putInt("manuallycounterforcourse$courseid", currentCounter).apply()

                            updateCounterText()
                        }
                        // Increment and save counter


                        isBackButtonEnabled = false
                        student_id.text?.clear()
                        Toast.makeText(this@ManuallyAttended, "Student ID added.", Toast.LENGTH_SHORT).show()

                        val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                        finish_for_manually_attend.background = drawable
                        finish_for_manually_attend.isClickable = true

                        if (finish_for_manually_attend.isClickable) {
                            finish_for_manually_attend.setOnClickListener {
                                val intent = Intent(this@ManuallyAttended, take_atten::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@ManuallyAttended,
                            "Invalid student ID: $studentId.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Coroutine", "Error retrieving data: ${e.message}")
            }
        }
    }

    private fun updateCounterText() {
        counter_0.text = "Counter: ${sharedPreferences.getInt("manuallycounterforcourse$courseid", 0)}"
    }
    private fun updateCounterTextforexam() {
        counter_0.text = "Counter: ${sharedPreferences.getInt("manuallycounterforexam$examId", 0)}"
    }

    override fun onBackPressed() {
        if (isBackButtonEnabled) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "please,finish the attend", Toast.LENGTH_SHORT).show()
        }
    }

}


