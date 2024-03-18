package eu.tutorials.shaproject

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_coursescreen.*
import kotlinx.android.synthetic.main.activity_manually_atten.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

class manually_atten : AppCompatActivity() {

    private val students = mutableListOf<String>()
    private val students_id = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_atten)
        students.clear()
        students_id.clear()
        var counter=0
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val studentApi = retrofit.create(StudentApi::class.java)

        rectangle_1.setOnClickListener {
            val studentId = student_id.text.toString()

            if (students_id.contains(studentId.toInt())) {
                Toast.makeText(this@manually_atten, "Student ID $studentId is already added.", Toast.LENGTH_SHORT).show()
            } else {
                val call = studentApi.getTeachers(studentId.toInt())
                call.enqueue(object : Callback<List<StudentResponse>?> {
                    override fun onResponse(call: Call<List<StudentResponse>?>, response: Response<List<StudentResponse>?>) {
                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                            val body = response.body()!!
                            val studentName = StringBuilder()

                            for (myData in body) {
                                studentName.append(myData.name)
                            }

                            val studentData = "$studentId, $studentName"
                            students_id.add(studentId.toInt())
                            students.add(studentData)
                            counter_0.text = "Counter: ${++counter}"
                            student_id.text?.clear()
                            Toast.makeText(this@manually_atten, "Student ID added.", Toast.LENGTH_SHORT).show()
                            val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                            rectangle_2.background = drawable
                            rectangle_2.isClickable = true

                            if (rectangle_2.isClickable) {
                                rectangle_2.setOnClickListener {
                                    val intent = Intent(this@manually_atten, take_atten::class.java)
                                    intent.putStringArrayListExtra("students", ArrayList(students))
                                    intent.putIntegerArrayListExtra("students_id", ArrayList(students_id))
                                    startActivity(intent)

                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<StudentResponse>?>, t: Throwable) {
                        Toast.makeText(this@manually_atten, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
                }



        }





    }
}