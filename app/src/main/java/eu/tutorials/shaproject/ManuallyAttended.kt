package eu.tutorials.shaproject

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_manually_atten.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

class ManuallyAttended : AppCompatActivity() {

    private val students = mutableListOf<String>()
    private val studentIds = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manually_atten)
        students.clear()
        studentIds.clear()
        var counter = 0
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val studentApi = retrofit.create(StudentApi::class.java)

        attend.setOnClickListener {
            val studentId = student_id.text.toString()

            if (studentIds.contains(studentId.toInt())) {
                Toast.makeText(
                    this@ManuallyAttended,
                    "Student ID $studentId is already added.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val call = studentApi.getTeachers(studentId.toInt())
                call.enqueue(object : Callback<List<StudentResponse>?> {
                    override fun onResponse(
                        call: Call<List<StudentResponse>?>,
                        response: Response<List<StudentResponse>?>
                    ) {
                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                            val body = response.body()!!
                            val studentName = StringBuilder()

                            for (myData in body) {
                                studentName.append(myData.name)
                            }

                            val studentData = "$studentId, $studentName"
                            studentIds.add(studentId.toInt())
                            students.add(studentData)
                            counter_0.text = getString(R.string.counter, (++counter).toString())
                            student_id.text?.clear()
                            Toast.makeText(
                                this@ManuallyAttended,
                                "Student ID added.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val drawable: Drawable? =
                                ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                            finish.background = drawable
                            finish.isClickable = true

                            if (finish.isClickable) {
                                finish.setOnClickListener {
                                    val intent = Intent(this@ManuallyAttended, take_atten::class.java)
                                    intent.putStringArrayListExtra("students", ArrayList(students))
                                    intent.putIntegerArrayListExtra(
                                        "students_id",
                                        ArrayList(studentIds)
                                    )
                                    startActivity(intent)

                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<StudentResponse>?>, t: Throwable) {
                        Toast.makeText(
                            this@ManuallyAttended,
                            "Request failed: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}