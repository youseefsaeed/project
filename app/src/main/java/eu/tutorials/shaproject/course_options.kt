package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.android.synthetic.main.activity_coursescreen.*

class course_options : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_options)

        btn_logout1.setOnClickListener{
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }

       var course_homepage1=intent.getStringExtra(Constants.course_name)
        course_homepage.text="${course_homepage1}" + " Home Page"
        var username_2=intent.getStringExtra(Constants.teacher_name)
        username2.text=username_2
        course_id_1.text="Course Id:${intent.getIntExtra(Constants.course_id,0)}"
        val sharedPreferences = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("course_id", intent.getIntExtra(Constants.course_id,0))
            .apply()
        btn_take_att.setOnClickListener{
            val intent= Intent(this,take_atten::class.java)
            startActivity(intent)
        }
        btn_lec_history.setOnClickListener{
            val intent= Intent(this,lecuters_history::class.java)
            startActivity(intent)
        }
        btn_metrics.setOnClickListener{
            val intent= Intent(this,students_metrics::class.java)
            startActivity(intent)
        }
    }
}