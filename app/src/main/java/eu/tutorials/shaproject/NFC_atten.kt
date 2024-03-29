package eu.tutorials.shaproject

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import eu.tutorials.shaproject.Constants.students
import kotlinx.android.synthetic.main.activity_manually_atten.*
import kotlinx.android.synthetic.main.activity_nfc_atten.counter_0
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.StringBuilder

class NFC_atten : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val retrofit: Retrofit = RetrofitClient.getRetrofitObject()
    private val studentApi = retrofit.create(StudentApi::class.java)
    private var pendingIntent: PendingIntent? = null
    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_atten)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

    }

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't have NFC.", Toast.LENGTH_SHORT).show()
            // Handle the absence of NFC adapter
        } else {
            val intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
            val techListsArray = arrayOf(arrayOf(Ndef::class.java.name))
            val flags = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or NfcAdapter.FLAG_READER_NFC_F
            nfcAdapter?.enableReaderMode(this, null, flags, null)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.let {
            val ndef = Ndef.get(tag)
            ndef.connect()
            val messages = ndef.ndefMessage
            messages?.records?.forEach { record ->
                val payload = String(record.payload, Charsets.UTF_8)
                val numericPayload = payload.replace(Regex("[^0-9]"), "")

                if (students.contains(numericPayload)) {
                    Toast.makeText(this@NFC_atten, "Student ID $numericPayload is already added.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@NFC_atten, "Student ID $numericPayload is already added.", Toast.LENGTH_SHORT).show()
                }

                finish.background = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                finish.isClickable = true
                Toast.makeText(
                    this,
                    "NFC card read successfully.\nCard ID: $numericPayload",
                    Toast.LENGTH_SHORT
                ).show()
                counter_0.text = getString(R.string.counter, (++counter).toString())

                if (finish.isClickable) {
                    finish.setOnClickListener {
                        val takeAttenIntent = Intent(this, take_atten::class.java)
                        startActivity(takeAttenIntent)
                    }
                }
            }
            ndef.close()
        }
    }



    private fun call(studentId:Int){
        val call = studentApi.getStudents(studentId)
        call.enqueue(object : Callback<List<StudentResponse>?> {
            override fun onResponse(call: Call<List<StudentResponse>?>, response: Response<List<StudentResponse>?>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val body = response.body()!!
                    val studentName = StringBuilder()

                    for (myData in body) {
                        studentName.append(myData.name)
                    }

                    val studentData = "$studentId, $studentName"

                    students.add(studentData)
                    counter_0.text = "Counter: ${++counter}"
                    student_id.text?.clear()
                    Toast.makeText(this@NFC_atten, "Student ID added.", Toast.LENGTH_SHORT).show()
                    val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                    finish.background = drawable
                    finish.isClickable = true

                    if (finish.isClickable) {
                        finish.setOnClickListener {
                            val intent = Intent(this@NFC_atten, take_atten::class.java)
                            startActivity(intent)

                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<StudentResponse>?>, t: Throwable) {
                Toast.makeText(this@NFC_atten, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
