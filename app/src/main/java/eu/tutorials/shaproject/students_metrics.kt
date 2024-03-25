package eu.tutorials.shaproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import eu.tutorials.shaproject.RetrofitClient.Companion.client
import kotlinx.android.synthetic.main.activity_course_options.*
import kotlinx.android.synthetic.main.activity_coursescreen.*
import kotlinx.android.synthetic.main.activity_students_metrics.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

        fetchStudentData(courseId)
        setupRecyclerView()
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

    private fun fetchStudentData(courseId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
         val apiService = retrofit.create(create_lecuture::class.java)
        val call3 = apiService.getStudentOfCourse(courseId)
        call3.enqueue(object : Callback<List<ApiResponseformetrics>> {
            override fun onResponse(call: Call<List<ApiResponseformetrics>>, response: Response<List<ApiResponseformetrics>>) {
                if (response.isSuccessful) {
                    val apiResponseList = response.body()
                    adapterStudent_metrics = AdapterStudent_metrics(baseContext, apiResponseList)
                    adapterStudent_metrics.courseClickListener =
                        object : AdapterStudent_metrics.CourseClickListener {
                            override fun onCourseClicked(studentId: Int, studentName: String,studentgrade:Int,studentfaculty:String,studentintended:Int) {
                                val intent = Intent(this@students_metrics, student_details::class.java)
                                intent.putExtra(Constants.student_id,studentId)
                                intent.putExtra(Constants.student_name,studentName)
                                intent.putExtra(Constants.student_attended,studentintended)
                                intent.putExtra(Constants.student_grade,studentgrade)
                                intent.putExtra(Constants.student_faculty,studentfaculty)
                                startActivity(intent)
                            }
                        }
                    adapterStudent_metrics.notifyDataSetChanged()
                    recycleview2.adapter = adapterStudent_metrics

                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<List<ApiResponseformetrics>>, t: Throwable) {
                Toast.makeText(
                    this@students_metrics,
                    "Request failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("API_CALL_ERROR", "Error occurred during API call", t)
            }
        })
    }

    private fun setupRecyclerView() {
        recycleview2.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recycleview2.layoutManager = linearLayoutManager
        adapterStudent_metrics = AdapterStudent_metrics(baseContext, emptyList())
        recycleview2.adapter = adapterStudent_metrics

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
