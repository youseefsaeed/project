package eu.tutorials.shaproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgIntroScreen.alpha =0f
        imgIntroScreen.animate().setDuration(2500).alpha(1f).withEndAction(){
            val i=Intent(this,Login::class.java)
            startActivity(i)
            finish()
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
    }
}