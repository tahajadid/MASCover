package tahadeta.example.mascover.ui.wallpaper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.WallpaperHelper
import java.io.File
import java.io.IOException


class DetailWallpaperFragment : Fragment() {

    private val args by navArgs<DetailWallpaperFragmentArgs>()
    lateinit var setWallpaperView: View
    lateinit var likeImage: ImageView
    lateinit var dislikeImage: ImageView
    lateinit var downloadVew: View
    lateinit var bitmapLast: Bitmap
    lateinit var imageWallpaper: ImageView
    lateinit var imagePhone: ImageView
    lateinit var imageTiger: ImageView

    lateinit var homeTv: TextView
    lateinit var lockTv: TextView
    lateinit var bothTv: TextView
    lateinit var chooseWallpaper: ConstraintLayout

    lateinit var viewloading: View
    lateinit var loading: LottieAnimationView

    var isShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail_wallpaper, container, false)

        imageWallpaper = root.findViewById(R.id.imageWallpaper)
        setWallpaperView = root.findViewById(R.id.viewSetWallpapers)
        likeImage = root.findViewById(R.id.likeImage)
        dislikeImage = root.findViewById(R.id.dislikeImage)
        downloadVew = root.findViewById(R.id.downloadView)
        imagePhone = root.findViewById(R.id.imagePhone)
        imageTiger = root.findViewById(R.id.imageTiger)
        homeTv = root.findViewById(R.id.homeTv)
        lockTv = root.findViewById(R.id.lockTv)
        bothTv = root.findViewById(R.id.bothTv)
        chooseWallpaper = root.findViewById(R.id.chooseWallpaper_Cl)
        viewloading = root.findViewById(R.id.viewloading)
        loading = root.findViewById(R.id.animationLoading)

        loading.visibility = View.GONE

        WallpaperHelper.setImage(imageWallpaper, args.idWallpaper)
        fadeToUp(imagePhone, 70F, 1000)

        Handler().postDelayed({
            imageWallpaper.visibility = View.VISIBLE
            imageTiger.visibility = View.VISIBLE
            fadeToUp(imageWallpaper, 20F, 300)
        }, 1200) // 3000 is the delayed time in milliseconds.

        initComponents()

        return root
    }

    private fun initComponents() {

        likeImage.setOnClickListener {
            likeImage.setImageResource(R.drawable.like_fill)
            dislikeImage.setImageResource(R.drawable.dislike)
        }

        dislikeImage.setOnClickListener {
            likeImage.setImageResource(R.drawable.like)
            dislikeImage.setImageResource(R.drawable.dislike_fill)
        }

        downloadVew.setOnClickListener {
            downloadImage(args.path4K)
        }

        setWallpaperView.setOnClickListener {
            if (isShow) {
                chooseWallpaper.visibility = View.GONE
                isShow = false
            } else {
                chooseWallpaper.visibility = View.VISIBLE
                isShow = true
            }
        }

        homeTv.setOnClickListener {
            tempoViewLoading()
            setWallpaperImage(1)
        }
        lockTv.setOnClickListener {
            tempoViewLoading()
            setWallpaperImage(2)
        }
        bothTv.setOnClickListener {
            tempoViewLoading()
            setWallpaperImage(3)
        }
    }


    private fun downloadImage(pathImage: String) {

        if (isStoragePermissionGranted()) {
            Toast.makeText(activity, Constants.DOWNLOAD_FR, Toast.LENGTH_SHORT).show()

            val refStorage = Firebase.storage.reference.child(pathImage)

            val localFile = File.createTempFile("tempImag", "jpg")
            refStorage.getFile(localFile).addOnCompleteListener {

                bitmapLast = BitmapFactory.decodeFile(localFile.absolutePath)

                MediaStore.Images.Media.insertImage(
                    requireActivity().getContentResolver(),
                    bitmapLast,
                    pathImage,
                    "Image"
                )

                Toast.makeText(activity, Constants.END_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Permission Rejetée, Activez la permission", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ResourceType", "MissingPermission")
    private fun setWallpaperImage(number: Int) {

        if (isWallpaperPermissionGranted()) {
            val wallpaperManager =
                WallpaperManager.getInstance(context)

            try {
                // set the wallpaper by calling the setResource function and
                // passing the drawable file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    if (bitmapLast == null) {
                        downloadImage(args.path4K)
                        setWallpaperImage(number)
                    } else {
                        when (number) {
                            1 -> wallpaperManager.setBitmap(bitmapLast, null, true, WallpaperManager.FLAG_SYSTEM)
                            2 -> wallpaperManager.setBitmap(bitmapLast, null, false, WallpaperManager.FLAG_LOCK)
                            3 -> {
                                wallpaperManager.setBitmap(bitmapLast, null, false, WallpaperManager.FLAG_LOCK)
                                wallpaperManager.setBitmap(bitmapLast, null, true, WallpaperManager.FLAG_SYSTEM)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                // here the errors can be logged instead of printStackTrace
                e.printStackTrace()
            }
        } else {
            Toast.makeText(activity, "Permission Rejetée, Activez la permission", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun isStoragePermissionGranted(): Boolean {
        val TAG = "Storage Permission"
        return if (Build.VERSION.SDK_INT >= 23) {
            if (context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                === PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted")
                true
            } else {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { // permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            true
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun isWallpaperPermissionGranted(): Boolean {
        val TAG = "Storage Permission"
        return if (Build.VERSION.SDK_INT >= 23) {
            if (context!!.checkSelfPermission(Manifest.permission.SET_WALLPAPER)
                === PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted")
                true
            } else {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { // permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            true
        }
    }

    private fun tempoViewLoading() {
                viewloading.visibility = View.VISIBLE
        loading.visibility = View.VISIBLE

        Handler().postDelayed({
            viewloading.visibility = View.GONE
            loading.visibility = View.GONE
        }, 1400) // 3000 is the delayed time in milliseconds.

        chooseWallpaper.visibility = View.GONE
        isShow = false
    }

    fun fadeToUp(view: View, spaceWithPixel: Float, duration: Long) {

        // Set button alpha to the 0
        view.alpha = 0F
        view.translationY = spaceWithPixel

        // Animate the alpha value to 1F and set duration
        view.animate().alpha(1F).translationYBy(-spaceWithPixel).setDuration(duration)
    }
}
