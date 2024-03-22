package eu.tutorials.shaproject

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import eu.tutorials.shaproject.Constants.students
import eu.tutorials.shaproject.RetrofitClient.Companion.getRetrofitObject
import kotlinx.android.synthetic.main.activity_manually_atten.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

class ManuallyAttended : AppCompatActivity() {


    private val students_id = mutableListOf<Int>()
    private val retrofit: Retrofit = getRetrofitObject()
    private val studentApi = retrofit.create(StudentApi::class.java)
    private var counter=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_atten)
        setListeners()

    }
    private fun setListeners() {

        attend.setOnClickListener {
            val studentId = student_id.text.toString()

            if (students_id.contains(studentId.toInt())) {
                Toast.makeText(this@ManuallyAttended, "Student ID $studentId is already added.", Toast.LENGTH_SHORT).show()
            } else{
                call(studentId.toInt())

            }
        }
    }


     private fun call(studentId:Int){
        val call = studentApi.getTeachers(studentId)
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
                    Toast.makeText(this@ManuallyAttended, "Student ID added.", Toast.LENGTH_SHORT).show()
                    val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                    finish.background = drawable
                    finish.isClickable = true

                    if (finish.isClickable) {
                        finish.setOnClickListener {
                            val intent = Intent(this@ManuallyAttended, take_atten::class.java)
                            intent.putIntegerArrayListExtra("students_id", ArrayList(students_id))
                            startActivity(intent)

                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<StudentResponse>?>, t: Throwable) {
                Toast.makeText(this@ManuallyAttended, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}