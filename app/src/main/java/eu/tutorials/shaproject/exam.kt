package eu.tutorials.shaproject

import android.app.DatePickerDialog
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
        rectangle_7.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        val exam_password = intent.getStringExtra(Constants.exam_pass)
        val exam_name = intent.getStringExtra(Constants.exam_name)
        exam_name_e.text = "Exam name: $exam_name"
        time_10_00_.text = "Time: ${intent.getStringExtra(Constants.exam_time)}"
        date_dd_mm_.text = "Date: ${intent.getStringExtra(Constants.exam_date)}"
        exam_name_e1.text = "Exam ID: ${intent.getStringExtra(Constants.exam_id)}"
        exam_invigi.text = "Exam invigilator: Ahmed"
        rectangle_4.setOnClickListener {
            if (enter_your_1.text.toString()!! == exam_password!!) {
                val call = ExamApi.getExam1(exam_name!!, enter_your_1.text.toString()!!)
                call.enqueue(object : Callback<List<examResponse2>?> {
                    override fun onResponse(
                        call: Call<List<examResponse2>?>,
                        response: Response<List<examResponse2>?>
                    ) {
                        if (response.isSuccessful) {
                            val intent = Intent(this@exam, take_atten::class.java)
                            startActivity(intent)
                            finish()
                        } else {

                        }
                    }

                    override fun onFailure(call: Call<List<examResponse2>?>, t: Throwable) {

                        Log.e("exam", "problem in  " + t.message)

                    }
                })
            }
            else{
                Toast.makeText(this@exam, "wrong password", Toast.LENGTH_SHORT)
                    .show()
            }
        }



    }

}