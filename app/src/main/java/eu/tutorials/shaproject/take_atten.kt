package eu.tutorials.shaproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.util.Date
import java.util.Locale

class take_atten : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_atten)
        val sharedPreferences = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val doctorId = sharedPreferences.getInt("doctor_id", 0)
        val sharedPreferences2 = this.getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        val courseid = sharedPreferences2.getInt("course_id", 0)

        nfc.setOnClickListener {
            val intent = Intent(this, NFC_atten::class.java)
            startActivity(intent)

        }
        manually.setOnClickListener {
            val intent = Intent(this, ManuallyAttended::class.java)
            startActivity(intent)

        }
        val studentIds = intent.getIntegerArrayListExtra("students_id")
        val students = intent.getStringArrayListExtra("students")
        val students2 = intent.getStringArrayListExtra("students2")
        val combinedList: ArrayList<String> = ArrayList()
        if (students != null) {
            combinedList.addAll(students)
        }
        if (students2 != null) {
            combinedList.addAll(students2)
        }

        if (students != null || students2 != null) {

            var finish = findViewById<View>(R.id.finish)
            finish.visibility = View.VISIBLE
            finish.setOnClickListener {

                createCSVFile(combinedList)

                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(logging)

                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.base_url) // Replace with your actual base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()

                val lectureService = retrofit.create(create_lecuture::class.java)
                val lectureDate = "2024-03-13"
                val lectureTime = "02:01:02.214Z"
                val lectureData = LectureData(lectureDate, lectureTime, doctorId, courseid)
                val call = lectureService.createLecture(lectureData)
                call.enqueue(object : retrofit2.Callback<LectureResponse?> {
                    override fun onResponse(
                        call: Call<LectureResponse?>,
                        response: retrofit2.Response<LectureResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val createLectureResponse = response.body()
                            val lectureId =
                                createLectureResponse?.lecture_id?.data?.get(0)?.lecture_id
                            val requestBody = RequestBody(
                                lecture_id = lectureId!!,
                                students = studentIds!!.toList()
                            )
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

        }
    }


    private fun createCSVFile(students: ArrayList<String>) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "students_$timeStamp.csv"

        if (checkPermission()) {
            val downloadsDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
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
