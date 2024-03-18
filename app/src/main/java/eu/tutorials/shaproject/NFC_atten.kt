package eu.tutorials.shaproject

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Drawable
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_manually_atten.*
import kotlinx.android.synthetic.main.activity_nfc_atten.*
import kotlinx.android.synthetic.main.activity_nfc_atten.counter_0
import kotlinx.android.synthetic.main.activity_nfc_atten.rectangle_2

class NFC_atten : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var cardIds: ArrayList<String> = ArrayList()
    private var pendingIntent: PendingIntent? = null
    private var counter=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_atten)
        cardIds.clear()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        try {
            val intent = Intent(this, javaClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        }
        catch (e: Exception){
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
                val numericPayload = payload.replace(Regex("[^0-9]"), "")
                cardIds.add(numericPayload)
                val drawable: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.rectangle_2222)
                rectangle_2.background = drawable
                rectangle_2.isClickable = true
                Toast.makeText(this, "NFC card read successfully.\nCard ID: $numericPayload", Toast.LENGTH_SHORT).show()
                counter_0.text="Counter:${++counter}"
                if(rectangle_2.isClickable) {
                    rectangle_2.setOnClickListener {
                        val intent = Intent(this, take_atten::class.java)
                        intent.putStringArrayListExtra("students2", ArrayList(cardIds))
                        startActivity(intent)
                    }
                }
            }

            ndef.close()
        }
    }


}
