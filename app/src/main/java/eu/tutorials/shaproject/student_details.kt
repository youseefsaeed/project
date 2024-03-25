package eu.tutorials.shaproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_student_details.*

class student_details : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)
        student_nam.text=intent.getStringExtra(Constants.student_name)
        artificial_.text=intent.getStringExtra(Constants.student_faculty)
        some_id.text=intent.getIntExtra(Constants.student_id,0).toString()
        four.text=intent.getIntExtra(Constants.student_grade,0).toString()
        someid.text=intent.getIntExtra(Constants.student_attended,0).toString()
    }
}