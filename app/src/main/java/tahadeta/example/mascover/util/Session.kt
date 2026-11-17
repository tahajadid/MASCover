package tahadeta.example.mascover.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tahadeta.example.mascover.data.Wallpaper

internal var isFrench = "fr"
internal var listFavourite: MutableList<Wallpaper> = ArrayList()
internal var updateShown = false
internal var languageDialogVisible = false
private var afterLanguagePrompt: (() -> Unit)? = null

internal fun runAfterLanguagePrompt(action: () -> Unit) {
    if (languageDialogVisible) {
        afterLanguagePrompt = action
    } else {
        action()
    }
}

internal fun finishLanguagePrompt() {
    languageDialogVisible = false
    val next = afterLanguagePrompt
    afterLanguagePrompt = null
    next?.invoke()
}

private const val FAVOURITES_KEY = "FAVOURITES"

fun loadFavourites() {
    val json = ModelPreferencesManager.preferences.getString(FAVOURITES_KEY, null) ?: return
    val type = object : TypeToken<MutableList<Wallpaper>>() {}.type
    val saved = Gson().fromJson<MutableList<Wallpaper>>(json, type) ?: return
    listFavourite.clear()
    listFavourite.addAll(saved)
}

fun addFavourite(item: Wallpaper) {
    if (listFavourite.any { it.idWallpaper == item.idWallpaper }) return
    listFavourite.add(item)
    saveFavourites()
}

fun removeFavourite(item: Wallpaper) {
    listFavourite.removeAll { it.idWallpaper == item.idWallpaper }
    saveFavourites()
}

private fun saveFavourites() {
    ModelPreferencesManager.preferences.edit()
        .putString(FAVOURITES_KEY, Gson().toJson(listFavourite))
        .apply()
}