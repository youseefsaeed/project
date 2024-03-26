package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import eu.tutorials.shaproject.db.AppDatabase
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.android.synthetic.main.activity_coursescreen.*
import kotlinx.android.synthetic.main.activity_students_metrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class students_metrics : AppCompatActivity() {
    private val sort = "Sort by:"
    private val name = "Name"
    private val high = "Higher attendance"
    private val low = "Lower attendance"
    private lateinit var spinner: Spinner
    private lateinit var adapterStudent_metrics: AdapterStudent_metrics
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_metrics)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val sharedPreferences2 = this.getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        val courseId = sharedPreferences2.getInt("course_id", 0)
        val searchView = findViewById<SearchView>(R.id.rectangle_1)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterStudent_metrics.search(newText.orEmpty())
                return true
            }
        })
        spinner = findViewById(R.id.rectangle_2)
        val list = arrayOf(sort, name, high, low)
        val arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        spinner.adapter = arrayAdapter


        setupRecyclerView()
        updateRecyclerView()
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    1 -> sortStudentsByName()
                    2 -> sortStudentsByHigherAttendance(courseId)
                    3 -> sortStudentsByLowerAttendance(courseId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected
            }
        }
    }



    private fun setupRecyclerView() {
        recycleview2.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recycleview2.layoutManager = linearLayoutManager
        adapterStudent_metrics = AdapterStudent_metrics(baseContext, emptyList())
        recycleview2.adapter = adapterStudent_metrics

    }
    private fun updateRecyclerView(){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val database = AppDatabase.getInstance(applicationContext)
                // Fetch all students from the database
                val students = database.studentDao().getAllStudents()

                // Update the RecyclerView with the fetched data
                withContext(Dispatchers.Main) {
                    adapterStudent_metrics = AdapterStudent_metrics(baseContext, students)
                    adapterStudent_metrics.courseClickListener =
                        object : AdapterStudent_metrics.CourseClickListener {
                            override fun onCourseClicked(
                                studentId: Int,
                                studentName: String,
                                studentgrade: Int,
                                studentfaculty: String,
                                studentintended: Int
                            ) {
                                val intent = Intent(this@students_metrics, student_details::class.java)
                                intent.putExtra(Constants.student_id, studentId)
                                intent.putExtra(Constants.student_name, studentName)
                                intent.putExtra(Constants.student_attended, studentintended)
                                intent.putExtra(Constants.student_grade, studentgrade)
                                intent.putExtra(Constants.student_faculty, studentfaculty)
                                startActivity(intent)
                            }
                        }
                    recycleview2.adapter = adapterStudent_metrics
                }
            } catch (e: Exception) {
                Log.e("Coroutine", "Error inserting data: ${e.message}")
            }
        }
    }
    private fun sortStudentsByName() {
        adapterStudent_metrics.sortByName()
    }
    private fun sortStudentsByHigherAttendance(studentId: Int) {
        adapterStudent_metrics.sortByHigherAttendance(studentId)
    }
    private fun sortStudentsByLowerAttendance(studentId: Int) {
        adapterStudent_metrics.sortByLowerAttendance(studentId)
    }

}

