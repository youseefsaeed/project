package eu.tutorials.shaproject

import android.hardware.biometrics.BiometricManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.*

class lecuters_history : AppCompatActivity() {
    private val selectitem="-Select a lecture-"
    private val ten="Lecture 10"
    private val nine9="Lecture 9"
    private val eight="Lecture 8"
    private val seven="Lecture 7"
    private val six="Lecture 6"
    private val five="Lecture 5"
    private val four="Lecture 4"
    private val three="Lecture 3"
    private val two="Lecture 2"
    private val one="Lecture 1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecuters_history)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN


        val spinner: Spinner =findViewById(R.id.spinner2)
        val layoutToShow: LinearLayout = findViewById(R.id.layoutToShow)
        val listoflectures = arrayOf(selectitem,ten,nine9,eight,seven,six,five,four,three,two,one)
        val arrayAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listoflectures)
        spinner.adapter=arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    layoutToShow.visibility = View.VISIBLE
                } else {
                    layoutToShow.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected
            }

        }
    }
}