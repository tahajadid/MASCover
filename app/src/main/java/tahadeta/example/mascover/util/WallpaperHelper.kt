package tahadeta.example.mascover.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

object WallpaperHelper {

    fun setImageForDetails(image: ImageView, imageUrl: String, context: Context, progressBar: ProgressBar, imageTiger : ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar.visibility = View.GONE
                    imageTiger.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar.visibility = View.GONE
                    imageTiger.visibility = View.VISIBLE
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(image)
    }

    fun setImage(image: ImageView, imageUrl: String, context: Context, progressBar: ProgressBar) {
        Glide.with(context)
            .load(imageUrl)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(image)
    }

    fun downloadBitmap(
        context: Context,
        imageUrl: String,
        onReady: (Bitmap) -> Unit,
        onError: () -> Unit,
    ) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onReady(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onError()
                }
            })
    }
}
