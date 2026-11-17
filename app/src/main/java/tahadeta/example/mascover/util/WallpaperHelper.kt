package tahadeta.example.mascover.util

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import tahadeta.example.mascover.R
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

    fun setImage(image: ImageView, imageUrl: String, context: Context, shimmer: View) {
        image.setImageDrawable(null)
        image.tag = imageUrl
        startShimmer(shimmer)
        Glide.with(image)
            .load(imageUrl)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    if (image.tag != imageUrl) return true
                    stopShimmer(shimmer)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    if (image.tag != imageUrl || resource == null) return true
                    image.setImageDrawable(resource)
                    stopShimmer(shimmer)
                    return true
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(image)
    }

    private fun startShimmer(shimmer: View) {
        shimmer.visibility = View.VISIBLE
        val band = shimmer.findViewById<View>(R.id.shimmerHighlight) ?: return
        val run = Runnable {
            if (!shimmer.isAttachedToWindow || shimmer.width == 0 || band.width == 0) return@Runnable
            (shimmer.getTag(R.id.shimmerHighlight) as? ObjectAnimator)?.cancel()
            val animator = ObjectAnimator.ofFloat(
                band,
                View.TRANSLATION_X,
                -band.width.toFloat(),
                shimmer.width.toFloat()
            ).apply {
                duration = 1000L
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                start()
            }
            shimmer.setTag(R.id.shimmerHighlight, animator)
        }
        if (shimmer.width > 0) {
            run.run()
        } else {
            shimmer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (shimmer.width == 0) return
                    if (shimmer.viewTreeObserver.isAlive) {
                        shimmer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    run.run()
                }
            })
        }
    }

    private fun stopShimmer(shimmer: View) {
        (shimmer.getTag(R.id.shimmerHighlight) as? ObjectAnimator)?.cancel()
        shimmer.setTag(R.id.shimmerHighlight, null)
        shimmer.visibility = View.GONE
    }

    fun downloadBitmap(
        context: Context,
        imageUrl: String,
        onReady: (Bitmap) -> Unit,
        onError: () -> Unit,
    ): Target<Bitmap> {
        val target = object : CustomTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onReady(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                onError()
            }
        }
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(target)
        return target
    }
}
