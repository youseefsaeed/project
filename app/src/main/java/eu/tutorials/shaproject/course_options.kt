package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_course_options.*

class course_options : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_options)

        logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        val courseHomepage = intent.getStringExtra(Constants.course_name)
        course_homepage.text = "${courseHomepage}" + " Home Page"
        val username_2 = intent.getStringExtra(Constants.teacher_name)
        username2.text = username_2
        course_id_1.text = "Course Id:${intent.getIntExtra(Constants.course_id, 0)}"
        val sharedPreferences = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("course_id", intent.getIntExtra(Constants.course_id, 0))
            .apply()
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