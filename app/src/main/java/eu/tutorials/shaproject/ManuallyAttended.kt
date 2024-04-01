package eu.tutorials.shaproject

import android.content.Intent
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

class ManuallyAttended : AppCompatActivity() {

    private var isBackButtonEnabled = true
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_atten)
        setListeners()

    }

    private fun setListeners() {

        attend.setOnClickListener {
            val studentId = student_id.text.toString()
            if(studentId.isEmpty()){
                Toast.makeText(
                    this@ManuallyAttended,
                    "please,write the student id",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else if (students_ids.contains(studentId.toInt())&&studentId.isNotEmpty()) {
                Toast.makeText(
                    this@ManuallyAttended,
                    "Student ID $studentId is already added.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                checkid(studentId.toInt())

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
                        counter_0.text = "Counter: ${++counter}"
                        isBackButtonEnabled = false
                        student_id.text?.clear()
                        Toast.makeText(this@ManuallyAttended, "Student ID added.", Toast.LENGTH_SHORT).show()
                        val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                        finish.background = drawable
                        finish.isClickable = true

                        if (finish.isClickable) {
                            finish.setOnClickListener {
                                val intent = Intent(this@ManuallyAttended, take_atten::class.java)
                                startActivity(intent)

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
    override fun onBackPressed() {
        if (isBackButtonEnabled) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "please,finish the attend", Toast.LENGTH_SHORT).show()
        }
    }
}

