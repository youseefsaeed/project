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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_atten)
        students.clear()
        var counter:Int=0
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val studentApi = retrofit.create(StudentApi::class.java)

        rectangle_1.setOnClickListener{
            val studentId = student_id.text.toString()
            val call = studentApi.getTeachers(studentId.toInt()!!)
            call.enqueue(object : Callback<List<StudentResponse>?> {
                override fun onResponse(call: Call<List<StudentResponse>?>, response: Response<List<StudentResponse>?>) {
                    if (response.isSuccessful && response.body()!!.isNotEmpty() ) {
                        if (studentId.isNotEmpty()) {
                            val body=response.body()!!
                            val student_name= StringBuilder()
                           // val student_grade= StringBuilder()
                           // val student_faculty= StringBuilder()
                            for (mydata in body){
                                student_name.append(mydata.name)
                               // student_grade.append(mydata.grade)
                               // student_faculty.append(mydata.faculty)
                            }

                            val studentData = "$studentId, $student_name"
                            students.add(studentData)
                            counter_0.text="Counter:${++counter}"
                            student_id.text?.clear()
                            Toast.makeText(this@manually_atten, "Student ID added.", Toast.LENGTH_SHORT).show()
                            val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                            rectangle_2.background = drawable
                            rectangle_2.isClickable = true
                            if(rectangle_2.isClickable) {
                                rectangle_2.setOnClickListener {
                                    val intent = Intent(this@manually_atten, take_atten::class.java)
                                    intent.putStringArrayListExtra("students", ArrayList(students))
                                    startActivity(intent)
                                }
                            }
                        }

                    }
                    else{
                        if (response.body()!!.isEmpty()){
                            Toast.makeText(this@manually_atten, "invalid id", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<StudentResponse>?>, t: Throwable) {

                    Log.e("manually_atten", "problem in  " + t.message)

                }
            })



        }





    }
}