package tahadeta.example.mascover.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import tahadeta.example.mascover.MainActivity
import tahadeta.example.mascover.R

class SplashActivity : AppCompatActivity() {

    lateinit var logo: ImageView
    lateinit var phone: ImageView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val window: Window = this.window
        if (window != null) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }

        Handler().postDelayed({
            if (!isOnline(applicationContext)) {
                showNoInternetPopup()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000) // 3000 is the delayed time in milliseconds.

        logo = findViewById(R.id.titleApp)
        phone = findViewById(R.id.phoneBottomImage)

        fadeToDown(logo, 50F, 1200)
        fadeToUp(phone, 50F, 1000)
    }

    fun fadeToDown(view: View, spaceWithPixel: Float, duration: Long) {
        // Set button alpha to the 0
        view.alpha = 0F
        view.translationY = -spaceWithPixel

        // Animate the alpha value to 1F and set duration
        view.animate().alpha(1F).translationYBy(spaceWithPixel).setDuration(duration)
    }

    fun fadeToUp(view: View, spaceWithPixel: Float, duration: Long) {
        // Set button alpha to the 0
        view.alpha = 0F
        view.translationY = spaceWithPixel

        // Animate the alpha value to 1F and set duration
        view.animate().alpha(1F).translationYBy(-spaceWithPixel).setDuration(duration)
    }

    private fun showNoInternetPopup() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.internet_popup_dialog)
        var okBtn = dialog.findViewById<TextView>(R.id.okBtn)

        okBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        val width = resources.displayMetrics.widthPixels * 0.80
        val height = resources.displayMetrics.heightPixels * 0.40

        dialog.window?.setLayout(width.toInt(), height.toInt())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}
