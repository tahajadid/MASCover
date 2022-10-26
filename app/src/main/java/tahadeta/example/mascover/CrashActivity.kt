package tahadeta.example.mascover

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.emirhankolver.GlobalExceptionHandler

class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        GlobalExceptionHandler.getThrowableFromIntent(intent).let {
            Log.e("ERRORHANDLER", "Error Data: ", it)
        }

        val window: Window = this.window

        if (window != null) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black))
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }

        findViewById<TextView>(R.id.backHome).setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}
