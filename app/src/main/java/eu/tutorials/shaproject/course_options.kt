package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import eu.tutorials.shaproject.db.AppDatabase
import eu.tutorials.shaproject.db.StudentEntity
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class course_options : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_options)
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        val courseHomepage = intent.getStringExtra(Constants.course_name)
        course_homepage.text = "$courseHomepage Home Page"

        val username = intent.getStringExtra(Constants.teacher_name)
        username2.text = username

        val courseId = intent.getIntExtra(Constants.course_id, 0)
        course_id_1.text = "Course Id: $courseId"

        sharedPreferences = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("course_id", courseId)
            .apply()
    }
    private fun setupListeners() {
        logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        take_attend.setOnClickListener {
            val intent = Intent(this, take_atten::class.java)
            startActivity(intent)
        }

        lectures_hi.setOnClickListener {
            val intent = Intent(this, lecuters_history::class.java)
            startActivity(intent)
        }

        students_me.setOnClickListener {
            val intent = Intent(this, students_metrics::class.java)
            startActivity(intent)
        }
    }
}