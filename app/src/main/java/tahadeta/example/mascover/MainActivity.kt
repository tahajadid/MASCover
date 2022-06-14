package tahadeta.example.mascover

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var navController: NavController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)

        ModelPreferencesManager.with(this.application)

        if (ModelPreferencesManager.get<Boolean>(Constants.FIRST_CON) == null) {
            ModelPreferencesManager.put(true, Constants.FIRST_CON)
            Log.d("FistTest", "FirstConn..")
        }
    }
}
