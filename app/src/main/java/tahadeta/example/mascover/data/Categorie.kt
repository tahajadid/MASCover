package tahadeta.example.mascover.data

import com.google.firebase.firestore.PropertyName

data class Categorie(
    val pathPoster: String ? = "",
    val title: String? = "",
    val weight: Int = 0,
    val idCategorie: String = "",
    @get:PropertyName("isVisible")
    @set:PropertyName("isVisible")
    var isVisible: Boolean = true
)

