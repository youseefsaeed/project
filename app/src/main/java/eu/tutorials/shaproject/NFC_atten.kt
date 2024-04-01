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
import android.util.Log
import androidx.core.content.ContextCompat
import android.widget.Toast
import eu.tutorials.shaproject.Constants.students
import eu.tutorials.shaproject.db.AppDatabase
import kotlinx.android.synthetic.main.activity_manually_atten.*
import kotlinx.android.synthetic.main.activity_manually_atten.finish
import kotlinx.android.synthetic.main.activity_nfc_atten.*
import kotlinx.android.synthetic.main.activity_nfc_atten.counter_0
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.StringBuilder
class NFC_atten : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var counter = 0
    private var isBackButtonEnabled = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_atten)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        try {
            val intent = Intent(this, javaClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "This device doesn't have NFC.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
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
                val numericPayload = payload.filter { it.isDigit() }


                if (students.contains(numericPayload)) {

                    Toast.makeText(
                        this@NFC_atten,
                        "Card ID $numericPayload is already added.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    checkid(numericPayload.toInt())
            }
            }
            ndef.close()
        }
    }




    private fun checkid(studentId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val database = AppDatabase.getInstance(applicationContext)
                // Fetch the student from the database
                val studentDao = database.studentDao().getStudentById(studentId)

                runOnUiThread {
                    if (studentDao != null) {
                        val studentName = studentDao.name
                        val studentid= studentDao.studentId
                        val studentData = "$studentid, $studentName"
                        students.add(studentData)
                        counter_0.text = "Counter: ${++counter}"
                        isBackButtonEnabled = false
                        Toast.makeText(this@NFC_atten, "Student ID added.", Toast.LENGTH_SHORT).show()
                        val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                        finish2.background = drawable
                        finish2.isClickable = true

                        if (finish2.isClickable) {
                            finish2.setOnClickListener {
                                val intent = Intent(this@NFC_atten, take_atten::class.java)
                                startActivity(intent)

                            }
                        }
                    } else {
                        Toast.makeText(
                            this@NFC_atten,
                            "Invalid student ID: $studentId.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Coroutine", "Error retrieving data: ${e.message}")
            }
        }
    }
    override fun onBackPressed() {
        if (isBackButtonEnabled) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "please,finish the attend", Toast.LENGTH_SHORT).show()
        }
    }
    }


