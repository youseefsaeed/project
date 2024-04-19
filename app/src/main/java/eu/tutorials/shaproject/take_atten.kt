package eu.tutorials.shaproject

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import eu.tutorials.shaproject.Constants.students
import eu.tutorials.shaproject.Constants.students_ids
import eu.tutorials.shaproject.RetrofitClient.Companion.getRetrofitObject
import kotlinx.android.synthetic.main.activity_take_atten.manually
import kotlinx.android.synthetic.main.activity_take_atten.nfc
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class take_atten : AppCompatActivity() {
    private lateinit var sharedPreferences3: SharedPreferences
    private var isBackButtonEnabled = true
    private val retrofit: Retrofit = getRetrofitObject()
    private val lectureService = retrofit.create(create_lecuture::class.java)
    private val retrofit2: Retrofit = getRetrofitObject()
    private val  apiService = retrofit2.create(create_lecuture::class.java)
    private lateinit var lectures: List<Lecture>
    private var  lectureId by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_atten)
        val sharedPreferences = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val doctorId = sharedPreferences.getInt("doctor_id", 0)
        val sharedPreferences2 = this.getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        val courseid = sharedPreferences2.getInt("course_id", 0)
        val code=sharedPreferences2.getInt("code",0)
        nfc.setOnClickListener {
            val intent = Intent(this, NFC_atten::class.java)
            startActivity(intent)

        }
        manually.setOnClickListener {
            val intent = Intent(this, ManuallyAttended::class.java)
            startActivity(intent)

        }
        call_for_get_all_lectures(doctorId,courseid)





        if (students.isNotEmpty() ) {
            isBackButtonEnabled = false
            var finish = findViewById<View>(R.id.finish3)
            finish.visibility = View.VISIBLE

            finish.setOnClickListener {
                students.clear()
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val lectureDate = dateFormat.format(calendar.time).toString()

                val existingLecture = lectures.find { it.lecture_date == lectureDate }
                
                if (existingLecture != null) {
                    // If a lecture with the current date exists, use its ID to add students
                    lectureId = existingLecture.lecture_id
                    val requestBody = RequestBody(
                        lecture_id = lectureId!!,
                        students = students_ids!!.toList()
                    )
                    Constants.students_ids.clear()
                    call_for_send_students_to_lecture(requestBody)
                } else {
                    // If not, create a new lecture
                    val lectureTime = SimpleDateFormat("HH:mm:ss.SSS'Z'", Locale.getDefault()).format(calendar.time).toString()
                    val lectureData = LectureData(lectureDate, lectureTime, doctorId, courseid)
                    call(lectureData)
                }



            if(code==1){
                val intent = Intent(this, course_options::class.java)
                startActivity(intent)
                finish()

            }
                else if(code==5){
                val intent = Intent(this, exam::class.java)
                startActivity(intent)
                finish()
                }



            }

        }
    }
    private fun call(lectureData: LectureData){
        val call = lectureService.createLecture(lectureData)
        call.enqueue(object : retrofit2.Callback<LectureResponse?> {
            override fun onResponse(
                call: Call<LectureResponse?>,
                response: retrofit2.Response<LectureResponse?>
            ) {
                if (response.isSuccessful) {
                    val createLectureResponse = response.body()
                     lectureId =
                         createLectureResponse?.lecture_id?.data?.get(0)?.lecture_id!!
                    val requestBody = RequestBody(
                        lecture_id = lectureId!!,
                        students = students_ids!!.toList()
                    )
                    Constants.students_ids.clear()
                    call_for_send_students_to_lecture(requestBody)

                } else {
                    val statusCode = response.code()
                    Toast.makeText(
                        this@take_atten,
                        "Request failed with status code $statusCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LectureResponse?>, t: Throwable) {
                Toast.makeText(
                    this@take_atten,
                    "Request failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun call_for_send_students_to_lecture(requestBody:RequestBody){
        val call2 = lectureService.createLectureWithStudents(requestBody)
        call2.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@take_atten, "error", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun call_for_get_all_lectures(doctorId:Int,courseid:Int){
        val call = apiService.getLectures(doctorId, courseid)
        call.enqueue(object : Callback<List<Lecture>> {
            override fun onResponse(call: Call<List<Lecture>>, response: Response<List<Lecture>>) {
                if (response.isSuccessful) {
                    lectures = response.body()!!

                } else {

                }
            }

            override fun onFailure(call: Call<List<Lecture>>, t: Throwable) {
                // Request failed
            }
        })
    }


    override fun onBackPressed() {
        if (isBackButtonEnabled) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "please,finish the attend", Toast.LENGTH_SHORT).show()
        }
    }

}