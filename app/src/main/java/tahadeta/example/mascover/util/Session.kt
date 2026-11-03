package tahadeta.example.mascover.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tahadeta.example.mascover.data.Wallpaper

internal var isFrench = "fr"
internal var listFavourite: MutableList<Wallpaper> = ArrayList()
internal var updateShown = false

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