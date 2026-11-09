package tahadeta.example.mascover.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeMode {

    fun apply(context: Context) {
        val dark = context
            .getSharedPreferences("FTCover", Context.MODE_PRIVATE)
            .getString(Constants.IS_DARK, null) == "true"
        val mode = if (dark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}
