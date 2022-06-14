package tahadeta.example.mascover.util

import android.graphics.BitmapFactory
import android.widget.ImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

object WallpaperHelper {

    fun setImage(image: ImageView, linkStorage: String) {

        val ref = Firebase.storage.reference.child(linkStorage)

        val localFile = File.createTempFile("tempImag", "jpg")
        ref.getFile(localFile).addOnCompleteListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
    }
}
