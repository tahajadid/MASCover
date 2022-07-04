package tahadeta.example.mascover.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object WallpaperHelper {

    fun setImage(image: ImageView, linkStorage: String, context: Context) {

        val ref = Firebase.storage.reference.child(linkStorage)

        ref.downloadUrl.addOnCompleteListener {
            Glide.with(context)
                .load(it.result)
                .into(image)
        }
    }
}
