package tahadeta.example.mascover.ui.wallpaper

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager
import tahadeta.example.mascover.util.WallpaperHelper
import java.io.File
import java.io.IOException

class DetailWallpaperFragment : Fragment() {

    private val args by navArgs<DetailWallpaperFragmentArgs>()
    lateinit var setWallpaperView: View
    lateinit var likeImage: ImageView
    lateinit var progressBarImage: ProgressBar
    lateinit var dislikeImage: ImageView
    lateinit var downloadVew: View
    var bitmapLast: Bitmap? = null
    lateinit var imageWallpaper: ImageView
    lateinit var imagePhone: ImageView
    lateinit var imageTiger: ImageView

    // For Demo
    lateinit var okOne: TextView
    lateinit var okTwo: TextView
    lateinit var demoOneCl: ConstraintLayout
    lateinit var demoTwoCl: ConstraintLayout

    lateinit var progressBar: ProgressBar
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
        progressBar = root.findViewById(R.id.progressBar)

        okOne = root.findViewById(R.id.okDemo_one)
        okTwo = root.findViewById(R.id.okDemo_two)
        demoOneCl = root.findViewById(R.id.demo_one_cl)
        demoTwoCl = root.findViewById(R.id.demo_two_cl)

        progressBarImage = root.findViewById(R.id.progressBarImage)

        if (ModelPreferencesManager.get<Boolean>(Constants.DEMO_SHOW) == null) {
            ModelPreferencesManager.put(true, Constants.DEMO_SHOW)
            showDemoOne()
        }

        loading.visibility = View.GONE

        WallpaperHelper.setImage(imageWallpaper, args.path4K, this.requireContext(),progressBarImage)
        fadeToUp(imagePhone, 70F, 1000)

        Handler().postDelayed({
            imageWallpaper.visibility = View.VISIBLE
            imageTiger.visibility = View.VISIBLE
            fadeToUp(imageWallpaper, 20F, 300)
        }, 1200) // 3000 is the delayed time in milliseconds.

        initComponents()

        return root
    }

    private fun showDemoOne() {
        demoOneCl.visibility = View.VISIBLE
        fadeToUp(demoOneCl, 20F, 300)

        okOne.setOnClickListener {
            demoOneCl.visibility = View.GONE
            showDemoTwo()
        }
        demoOneCl.setOnClickListener {
            demoOneCl.visibility = View.GONE
            showDemoTwo()
        }
    }

    private fun showDemoTwo() {
        demoTwoCl.visibility = View.VISIBLE
        fadeToUp(demoTwoCl, 20F, 300)

        okTwo.setOnClickListener {
            demoTwoCl.visibility = View.GONE
        }
        demoTwoCl.setOnClickListener {
            demoTwoCl.visibility = View.GONE
        }
    }

    private fun initComponents() {

        likeImage.setOnClickListener {
            likeImage.setImageResource(R.drawable.like_fill)
            dislikeImage.setImageResource(R.drawable.dislike)
            incrementLike()
        }

        dislikeImage.setOnClickListener {
            likeImage.setImageResource(R.drawable.like)
            dislikeImage.setImageResource(R.drawable.dislike_fill)
            incrementDislike()
        }

        downloadVew.setOnClickListener {
            downloadImage(args.path4K)
            incrementDownloads()
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
            progressBar.visibility = View.VISIBLE
            tempoViewLoading()
            setWallpaperImage(1)
            incrementDownloads()
        }
        lockTv.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            tempoViewLoading()
            setWallpaperImage(2)
            incrementDownloads()
        }
        bothTv.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            tempoViewLoading()
            setWallpaperImage(3)
            incrementDownloads()
        }
    }

    private fun incrementDownloads() {
        var database = FirebaseFirestore.getInstance()
        val washingtonRef: DocumentReference = database.collection("wallpaper").document(args.idWallpaper)
        washingtonRef.update("numberDownload", FieldValue.increment(1))
    }

    private fun incrementLike() {
        var database = FirebaseFirestore.getInstance()
        val washingtonRef: DocumentReference = database.collection("wallpaper").document(args.idWallpaper)
        washingtonRef.update("numberLike", FieldValue.increment(1))
    }

    private fun incrementDislike() {
        var database = FirebaseFirestore.getInstance()
        val washingtonRef: DocumentReference = database.collection("wallpaper").document(args.idWallpaper)
        washingtonRef.update("numberDislike", FieldValue.increment(1))
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
            Toast.makeText(activity, "Activez la permission", Toast.LENGTH_SHORT).show()
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
                        downloadAndSet(number)
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
                    progressBar.visibility = View.GONE
                }
            } catch (e: IOException) {
                progressBar.visibility = View.GONE
                // here the errors can be logged instead of printStackTrace
                e.printStackTrace()
            }
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(activity, "Permission Rejetée, Activez la permission", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun downloadAndSet(number: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isWallpaperPermissionGranted()) {

                if (isStoragePermissionGranted()) {
                    Toast.makeText(activity, Constants.DOWNLOAD_FR, Toast.LENGTH_SHORT).show()

                    val refStorage = Firebase.storage.reference.child(args.path4K)

                    val localFile = File.createTempFile("tempImag", "jpg")
                    refStorage.getFile(localFile).addOnCompleteListener {

                        bitmapLast = BitmapFactory.decodeFile(localFile.absolutePath)

                        MediaStore.Images.Media.insertImage(
                            requireActivity().getContentResolver(),
                            bitmapLast,
                            args.path4K,
                            "Image"
                        )

                        val wallpaperManager =
                            WallpaperManager.getInstance(context)

                        when (number) {
                            1 -> wallpaperManager.setBitmap(bitmapLast, null, true, WallpaperManager.FLAG_SYSTEM)
                            2 -> wallpaperManager.setBitmap(bitmapLast, null, false, WallpaperManager.FLAG_LOCK)
                            3 -> {
                                wallpaperManager.setBitmap(bitmapLast, null, false, WallpaperManager.FLAG_LOCK)
                                wallpaperManager.setBitmap(bitmapLast, null, true, WallpaperManager.FLAG_SYSTEM)
                            }
                        }

                        Toast.makeText(activity, Constants.END_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Permission Rejetée, Activez la permission", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(activity, Constants.JUST_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
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
        }, 3000) // 3000 is the delayed time in milliseconds.

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
