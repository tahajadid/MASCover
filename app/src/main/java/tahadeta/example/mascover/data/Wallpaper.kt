package tahadeta.example.mascover.data

data class Wallpaper(
    val idWallpaper: String ? = "",
    val idCategorie: String ? = "",
    val numberDownload: Int = 0,
    val numberLike: Int = 0,
    val numberDislike: Int = 0,
    val pathPoster: String ? = "",
    val pathPoster4K: String ? = ""
)
