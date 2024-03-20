package eu.tutorials.shaproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.android.synthetic.main.activity_students_metrics.*

class students_metrics : AppCompatActivity() {
    private val sort="Sort by: Name"
    private val name="Name"
    private val high="Higher attendance"
    private val low="Lower attendance"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_metrics)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN
        val spinner: Spinner =findViewById(R.id.rectangle_2)
        val list= arrayOf(sort,name,high,low)
        val arrayAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list)
        spinner.adapter=arrayAdapter

        student_name.setOnClickListener{
            val intent= Intent(this,student_details::class.java)
            startActivity(intent)
        }

    }
}
