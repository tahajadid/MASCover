package tahadeta.example.mascover.data

import com.google.firebase.firestore.PropertyName

data class Wallpaper(
    val idWallpaper: String ? = "",
    val idCategorie: String ? = "",
    val numberDownload: Int = 0,
    val numberLike: Int = 0,
    val numberDislike: Int = 0,
    val pathPoster: String ? = "",
    val pathPoster4K: String ? = "",
    @get:PropertyName("isNew")
    @set:PropertyName("isNew")
    var isNew: Boolean = false,
    @get:PropertyName("Type")
    @set:PropertyName("Type")
    var type: String = ""
)
