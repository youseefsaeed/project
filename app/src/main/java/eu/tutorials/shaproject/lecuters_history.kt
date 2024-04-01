package eu.tutorials.shaproject

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.*
import eu.tutorials.shaproject.RetrofitClient.Companion.getRetrofitObject
import kotlinx.android.synthetic.main.activity_lecuters_history.*
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

class lecuters_history : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 123
    private lateinit var lectures: List<Lecture>
    private lateinit var spinner: Spinner
    private lateinit var layoutToShow: LinearLayout
    private val retrofit: Retrofit = getRetrofitObject()
    private val  apiService = retrofit.create(create_lecuture::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecuters_history)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN
        val sharedPreferences = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val doctorId = sharedPreferences.getInt("doctor_id", 0)
        val sharedPreferences2 = this.getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        val courseid = sharedPreferences2.getInt("course_id", 0)
        spinner = findViewById(R.id.spinner2)
        layoutToShow = findViewById(R.id.layoutToShow)
        call(doctorId,courseid)




    }
    private fun call(doctorId:Int,courseid:Int){
        val call = apiService.getLectures(doctorId, courseid)
        call.enqueue(object : Callback<List<Lecture>> {
            override fun onResponse(call: Call<List<Lecture>>, response: Response<List<Lecture>>) {
                if (response.isSuccessful) {
                    lectures = response.body()!!
                    if (lectures != null) {
                        for (lecture in lectures) {
                            val lectureIds = lectures.map { it.lecture_id } // Extract lecture IDs
                            updateSpinner(lectureIds)

                        }
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<List<Lecture>>, t: Throwable) {
                // Request failed
            }
        })
    }
    private fun updateSpinner(lectureIds: List<Int>) {
        spinner = findViewById(R.id.spinner2)
        layoutToShow = findViewById(R.id.layoutToShow)
        val listoflectures = listOf("-Select a lecture-") + lectureIds.map { "lecture $it" }
        val arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listoflectures)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    layoutToShow.visibility = View.VISIBLE
                    val selectedLecture = lectures[position - 1]
                    val lectureDateTextView: TextView = findViewById(R.id.lecture_date)
                    val lectureTimeTextView: TextView = findViewById(R.id.lecture_time)
                    val studentCountTextView: TextView = findViewById(R.id.number_of_student)

                    lectureDateTextView.text = "Date: ${selectedLecture.lecture_date}"
                    lectureTimeTextView.text = "Time: ${selectedLecture.lecture_time}"
                    studentCountTextView.text = "Number of students attended: ${selectedLecture.studentcount}"

                    val button: Button = findViewById(R.id.button)
                    button.setOnClickListener {
                        val call3 = apiService.getStudent(selectedLecture.lecture_id)
                        call3.enqueue(object : Callback<List<ApiResponse>> {
                            override fun onResponse(call: Call<List<ApiResponse>>, response: Response<List<ApiResponse>>) {
                                if (response.isSuccessful) {
                                    val apiResponseList = response.body()
                                    if (apiResponseList != null) {
                                        val studentList = apiResponseList.map { it.students }
                                            .map { student ->
                                                " ${student.student_id} , ${student.name}"
                                            }

                                        createCSVFile(studentList as ArrayList<String>)
                                    }
                                } else {

                                }
                            }

                            override fun onFailure(call: Call<List<ApiResponse>>, t: Throwable) {
                                Toast.makeText(
                                    this@lecuters_history,
                                    "Request failed: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("API_CALL_ERROR", "Error occurred during API call", t)}
                        })
                    }

                } else {
                    layoutToShow.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle nothing selected event
            }
        }
    }



    private fun createCSVFile(students: ArrayList<String>) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "students_$timeStamp.csv"

        if (checkPermission()) {
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, fileName)

            try {
                val fileOutputStream = FileOutputStream(file)
                val header = "Student ID,Name"
                fileOutputStream.write(header.toByteArray())

                students.forEach { student ->
                    fileOutputStream.write("\n".toByteArray())
                    fileOutputStream.write(student.toByteArray())
                }
                fileOutputStream.close()
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(file)
                sendBroadcast(mediaScanIntent)

                Toast.makeText(this, "CSV file created successfully.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }



    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(
                        this,
                        "Permission granted. Click again to create CSV file.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Permission granted. Click again to create CSV file.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
