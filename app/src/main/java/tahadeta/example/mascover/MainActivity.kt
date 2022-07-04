package tahadeta.example.mascover

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import com.zeugmasolutions.localehelper.Locales
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager
import java.util.*

class MainActivity : AppCompatActivity() {

    private val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

    override fun getDelegate() = localeDelegate.getAppCompatDelegate(super.getDelegate())

    companion object {
        lateinit var navController: NavController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeDelegate.onCreate(this)
        setContentView(R.layout.activity_main)

        navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)

        ModelPreferencesManager.with(this.application)

        if (ModelPreferencesManager.get<Boolean>(Constants.FIRST_CON) == null) {
            ModelPreferencesManager.put(true, Constants.FIRST_CON)
            Log.d("FistTest", "FirstConn..")
            showLanguePopup()
        }

        if (ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH) == null) {
            ModelPreferencesManager.put(true, Constants.IS_FRENSH)
        }
    }

    private fun showLanguePopup() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_dialog)
        var arBtn = dialog.findViewById<TextView>(R.id.arab_Btn)
        var frBtn = dialog.findViewById<TextView>(R.id.frensh_Btn)
        var dimissTv = dialog.findViewById<TextView>(R.id.dimiss_tv)

        arBtn.setOnClickListener {
            updateLocale(Locales.Arabic)
            dialog.dismiss()
        }

        frBtn.setOnClickListener {
            dialog.dismiss()
        }

        dimissTv.setOnClickListener {
            dialog.dismiss()
        }

        val width = resources.displayMetrics.widthPixels * 0.90
        val height = resources.displayMetrics.heightPixels * 0.40

        dialog.window?.setLayout(width.toInt(), height.toInt())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase))
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        return LocaleHelper.onAttach(context)
    }

    override fun getApplicationContext(): Context =
        localeDelegate.getApplicationContext(super.getApplicationContext())

    open fun updateLocale(locale: Locale) {
        localeDelegate.setLocale(this, locale)
    }
}
