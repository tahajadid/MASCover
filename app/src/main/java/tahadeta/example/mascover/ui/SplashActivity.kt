package tahadeta.example.mascover.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import tahadeta.example.mascover.MainActivity
import tahadeta.example.mascover.R

class SplashActivity : AppCompatActivity() {

    lateinit var logo:ImageView
    lateinit var phone: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 3000 is the delayed time in milliseconds.

        logo = findViewById(R.id.titleApp)
        phone = findViewById(R.id.phoneBottomImage)

        fadeToDown(logo,50F, 1200)
        fadeToUp(phone,50F, 1000)

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

}
