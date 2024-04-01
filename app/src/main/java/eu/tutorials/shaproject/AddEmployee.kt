package eu.tutorials.shaproject

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.nfc.*
import android.nfc.NdefRecord.TNF_MIME_MEDIA
import android.nfc.tech.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_addemployee.*
import java.io.IOException
import android.nfc.NfcAdapter
import java.io.UnsupportedEncodingException
import java.util.*


class AddEmployee : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addemployee)

        logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    private fun writeStudentIdToNfcCard(tag: Tag, studentId: String) {
        try {
            val ndef = Ndef.get(tag)
            ndef?.connect()

            if (ndef != null && ndef.isWritable) {
                val ndefMessage = NdefMessage(NdefRecord.createTextRecord(null, studentId))
                ndef.writeNdefMessage(ndefMessage)
                Toast.makeText(this, "ID written to NFC card successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "NFC card is read-only or not supported.", Toast.LENGTH_SHORT).show()
            }

            ndef?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error writing ID to NFC card: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableNFCForegroundDispatch(activity: Activity, nfcAdapter: NfcAdapter?) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) ?: return

            val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_1111)
            rectangle_1111.background = drawable
            rectangle_1111.isClickable = true

            if (rectangle_1111.isClickable) {
                rectangle_1111.setOnClickListener {
                    val studentId = student_id.text.toString()
                    writeStudentIdToNfcCard(tag, studentId)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (nfcAdapter != null) {
            enableNFCForegroundDispatch(this, nfcAdapter)
        } else {
            Toast.makeText(this, "NFC adapter is not available on this device.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun enableNFCForegroundDispatch(activity: Activity, nfcAdapter: NfcAdapter?) {
        val intent = Intent(activity, activity.javaClass).apply {
            addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
            action = NfcAdapter.ACTION_TAG_DISCOVERED
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE)
        val intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        val techListsArray = arrayOf(arrayOf<String>())

        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techListsArray)
    }
    override fun onPause() {
        super.onPause()
        disableNFCForegroundDispatch(this, nfcAdapter)
    }
}









