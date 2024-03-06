package eu.tutorials.shaproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.android.synthetic.main.activity_course_options.btn_logout1
import kotlinx.android.synthetic.main.activity_nfc_atten.*
import kotlinx.android.synthetic.main.activity_take_atten.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class take_atten : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_atten)

        rectangle_4.setOnClickListener{
            val intent= Intent(this,NFC_atten::class.java)
            startActivity(intent)

        }
        rectangle_5.setOnClickListener{
            val intent= Intent(this,manually_atten::class.java)
            startActivity(intent)

        }
        val students = intent.getStringArrayListExtra("students")

        if (students != null) {
            var finish = findViewById(R.id.rectangle_22) as View
            finish.visibility = View.VISIBLE
            var finish2 = findViewById(R.id.finish) as View
            finish2.visibility = View.VISIBLE
            finish.setOnClickListener {
                createCSVFile(students)

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
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Click again to create CSV file.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    }
