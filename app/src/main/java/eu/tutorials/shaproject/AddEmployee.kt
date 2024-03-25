package eu.tutorials.shaproject

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_addemployee.*

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
                val ndefMessage = NdefMessage(NdefRecord.createTextRecord(null,studentId))
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
            val intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
            val techListsArray = arrayOf(arrayOf<String>())
            val flags = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            nfcAdapter?.enableReaderMode(this, null, flags, null)
        } else {
            Toast.makeText(this, "NFC adapter is not available on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    private fun enableNFCForegroundDispatch(activity: Activity, nfcAdapter: NfcAdapter?) {
        val intent = Intent(activity, activity.javaClass).apply {
            addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
            action = NfcAdapter.ACTION_TAG_DISCOVERED
        }

        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)
        val intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        val techListsArray = arrayOf(arrayOf<String>())

        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techListsArray)
    }
}