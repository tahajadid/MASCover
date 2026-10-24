package tahadeta.example.mascover.ui.wallpaper

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager
import tahadeta.example.mascover.util.WallpaperHelper
import java.io.IOException

class DetailWallpaperFragment : Fragment() {

    private val args by navArgs<DetailWallpaperFragmentArgs>()
    lateinit var setWallpaperView: View
    lateinit var likeImage: ImageView
    lateinit var progressBarImage: ProgressBar
    lateinit var dislikeImage: ImageView
    lateinit var downloadVew: View
    var bitmapLast: Bitmap? = null
    lateinit var backImage: ImageView
    lateinit var imageWallpaper: ImageView
    lateinit var imagePhone: ImageView
    lateinit var imageTiger: ImageView

    // For Demo
    lateinit var okOne: TextView
    lateinit var okTwo: TextView
    lateinit var demoOneCl: ConstraintLayout
    lateinit var demoTwoCl: ConstraintLayout

    lateinit var progressBar: ProgressBar
    lateinit var wallpaperOptions: View
    lateinit var sheetLoader: View
    lateinit var sheetLoaderText: TextView
    lateinit var optionHome: View
    lateinit var optionLock: View
    lateinit var optionBoth: View
    lateinit var optionHomeIcon: View
    lateinit var optionLockIcon: View
    lateinit var optionBothIcon: View
    lateinit var sheetHandle: View

    lateinit var viewloading: View

    var isShow = false
    private var isBusy = false
    private var selectedTarget = 3
    private var activeDownload: Target<Bitmap>? = null
    private var workStartedAt = 0L
    private var workFinished = false

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
        wallpaperOptions = root.findViewById(R.id.wallpaperOptions)
        sheetLoader = root.findViewById(R.id.sheetLoader)
        sheetLoaderText = root.findViewById(R.id.sheetLoaderText)
        optionHome = root.findViewById(R.id.optionHome)
        optionLock = root.findViewById(R.id.optionLock)
        optionBoth = root.findViewById(R.id.optionBoth)
        optionHomeIcon = root.findViewById(R.id.optionHomeIcon)
        optionLockIcon = root.findViewById(R.id.optionLockIcon)
        optionBothIcon = root.findViewById(R.id.optionBothIcon)
        sheetHandle = root.findViewById(R.id.sheetHandle)
        viewloading = root.findViewById(R.id.viewloading)
        progressBar = root.findViewById(R.id.progressBar)
        backImage = root.findViewById(R.id.backImage)

        okOne = root.findViewById(R.id.okDemo_one)
        okTwo = root.findViewById(R.id.okDemo_two)
        demoOneCl = root.findViewById(R.id.demo_one_cl)
        demoTwoCl = root.findViewById(R.id.demo_two_cl)

        progressBarImage = root.findViewById(R.id.progressBarImage)

        if (ModelPreferencesManager.get<Boolean>(Constants.DEMO_SHOW) == null) {
            ModelPreferencesManager.put(true, Constants.DEMO_SHOW)
            showDemoOne()
        }

        if (ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH) == false) {
            backImage.setImageResource(R.drawable.right_arrow)
        }
        backImage.setOnClickListener {
            if (isBusy) return@setOnClickListener
            if (isShow) {
                closeSheet()
            } else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isBusy) return
                    if (isShow) {
                        closeSheet()
                    } else {
                        isEnabled = false
                        findNavController().navigateUp()
                    }
                }
            }
        )


        WallpaperHelper.setImageForDetails(
            imageWallpaper,
            args.path4K,
            this.requireContext(),
            progressBarImage,
            imageTiger)
        fadeToUp(imagePhone, 70F, 1000)

        Handler().postDelayed({
            imageWallpaper.visibility = View.VISIBLE
            //imageTiger.visibility = View.VISIBLE
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
            likeImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.define_yellow))
            dislikeImage.clearColorFilter()
            incrementLike()
        }

        dislikeImage.setOnClickListener {
            dislikeImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.define_yellow))
            likeImage.clearColorFilter()
            incrementDislike()
        }

        downloadVew.setOnClickListener {
            if (isBusy) return@setOnClickListener
            if (needsLegacyStoragePermission() && !isStoragePermissionGranted()) {
                Toast.makeText(activity, "Activez la permission", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showWorkingSheet(getString(R.string.loading_download), highlightDownload = true)
            sheetLoader.post {
                if (!isAdded) return@post
                downloadImage(args.path4K)
                incrementDownloads()
            }
        }

        setWallpaperView.setOnClickListener {
            if (isBusy) return@setOnClickListener
            if (isShow) {
                showWorkingSheet(getString(R.string.loading_wallpaper), highlightDownload = false)
                sheetLoader.post {
                    if (!isAdded) return@post
                    setWallpaperImage(selectedTarget)
                    incrementDownloads()
                }
            } else {
                openSheet()
            }
        }

        optionHome.setOnClickListener { if (!isBusy) selectTarget(1) }
        optionLock.setOnClickListener { if (!isBusy) selectTarget(2) }
        optionBoth.setOnClickListener { if (!isBusy) selectTarget(3) }
        sheetHandle.setOnClickListener {
            if (!isBusy && isShow) closeSheet()
        }
    }

    private fun openSheet() {
        sheetLoader.visibility = View.GONE
        wallpaperOptions.visibility = View.VISIBLE
        wallpaperOptions.alpha = 0f
        wallpaperOptions.translationY = 24f
        wallpaperOptions.animate().alpha(1f).translationY(0f).setDuration(180).start()
        isShow = true
        setWallpaperView.setBackgroundResource(R.drawable.bg_define_button_active)
        updateSelection()
    }

    private fun showWorkingSheet(message: String, highlightDownload: Boolean) {
        workStartedAt = SystemClock.elapsedRealtime()
        workFinished = false
        wallpaperOptions.visibility = View.GONE
        sheetLoaderText.text = message
        sheetLoader.visibility = View.VISIBLE
        isShow = true
        isBusy = true
        setWallpaperView.isEnabled = false
        downloadVew.isEnabled = false
        if (highlightDownload) {
            setWallpaperView.setBackgroundResource(R.drawable.bg_define_button_idle)
            downloadVew.setBackgroundResource(R.drawable.bg_circle_yellow)
        } else {
            setWallpaperView.setBackgroundResource(R.drawable.bg_define_button_active)
            downloadVew.setBackgroundResource(R.drawable.bg_circle_white)
        }
    }

    private fun finishWorkingSheet() {
        if (!isAdded || workFinished) return
        workFinished = true
        val remaining = 600L - (SystemClock.elapsedRealtime() - workStartedAt)
        sheetLoader.postDelayed({
            if (!isAdded) return@postDelayed
            progressBar.visibility = View.GONE
            viewloading.visibility = View.GONE
            closeSheet()
        }, remaining.coerceAtLeast(0L))
    }

    private fun closeSheet() {
        wallpaperOptions.visibility = View.GONE
        sheetLoader.visibility = View.GONE
        isShow = false
        isBusy = false
        setWallpaperView.isEnabled = true
        downloadVew.isEnabled = true
        setWallpaperView.setBackgroundResource(R.drawable.bg_define_button_idle)
        downloadVew.setBackgroundResource(R.drawable.bg_circle_white)
    }

    private fun selectTarget(target: Int) {
        selectedTarget = target
        updateSelection()
    }

    private fun updateSelection() {
        optionHome.setBackgroundResource(
            if (selectedTarget == 1) R.drawable.bg_option_card_selected else R.drawable.bg_option_card
        )
        optionLock.setBackgroundResource(
            if (selectedTarget == 2) R.drawable.bg_option_card_selected else R.drawable.bg_option_card
        )
        optionBoth.setBackgroundResource(
            if (selectedTarget == 3) R.drawable.bg_option_card_selected else R.drawable.bg_option_card
        )
        optionHomeIcon.setBackgroundResource(
            if (selectedTarget == 1) R.drawable.bg_icon_circle_selected else R.drawable.bg_icon_circle
        )
        optionLockIcon.setBackgroundResource(
            if (selectedTarget == 2) R.drawable.bg_icon_circle_selected else R.drawable.bg_icon_circle
        )
        optionBothIcon.setBackgroundResource(
            if (selectedTarget == 3) R.drawable.bg_icon_circle_selected else R.drawable.bg_icon_circle
        )
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
        activeDownload = WallpaperHelper.downloadBitmap(
            requireContext(),
            pathImage,
            onReady = { bitmap ->
                if (!isAdded) return@downloadBitmap
                bitmapLast = bitmap
                try {
                    saveImage(bitmap)
                    Toast.makeText(activity, Constants.END_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Échec du téléchargement", Toast.LENGTH_SHORT).show()
                }
                activeDownload = null
                finishWorkingSheet()
            },
            onError = {
                if (!isAdded) return@downloadBitmap
                activeDownload = null
                Toast.makeText(activity, "Échec du téléchargement", Toast.LENGTH_SHORT).show()
                finishWorkingSheet()
            }
        )
    }

    private fun needsLegacyStoragePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    }

    private fun saveImage(bitmap: Bitmap) {
        val resolver = requireActivity().contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "mascover_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MASCover")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Unable to create image")
            resolver.openOutputStream(uri)?.use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Unable to write image")
                }
            } ?: throw IOException("Unable to open image")
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } else {
            MediaStore.Images.Media.insertImage(resolver, bitmap, "mascover", "Image")
        }
    }

    @SuppressLint("ResourceType", "MissingPermission")
    private fun setWallpaperImage(number: Int) {

        if (!isWallpaperPermissionGranted()) {
            Toast.makeText(activity, "Permission Rejetée, Activez la permission", Toast.LENGTH_SHORT).show()
            finishWorkingSheet()
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(activity, Constants.JUST_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
            finishWorkingSheet()
            return
        }

        try {
            if (bitmapLast == null) {
                downloadAndSet(number)
            } else {
                applyWallpaper(number)
                finishWorkingSheet()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            finishWorkingSheet()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyWallpaper(number: Int) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        when (number) {
            1 -> wallpaperManager.setBitmap(bitmapLast, null, true, WallpaperManager.FLAG_SYSTEM)
            2 -> wallpaperManager.setBitmap(bitmapLast, null, false, WallpaperManager.FLAG_LOCK)
            3 -> {
                wallpaperManager.setBitmap(bitmapLast, null, false, WallpaperManager.FLAG_LOCK)
                wallpaperManager.setBitmap(bitmapLast, null, true, WallpaperManager.FLAG_SYSTEM)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun downloadAndSet(number: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isWallpaperPermissionGranted()) {

                activeDownload = WallpaperHelper.downloadBitmap(
                    requireContext(),
                    args.path4K,
                    onReady = { bitmap ->
                        if (!isAdded) return@downloadBitmap
                        bitmapLast = bitmap
                        try {
                            saveImage(bitmap)
                            applyWallpaper(number)
                            Toast.makeText(activity, Constants.END_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(activity, "Échec du téléchargement", Toast.LENGTH_SHORT).show()
                        }
                        activeDownload = null
                        finishWorkingSheet()
                    },
                    onError = {
                        if (!isAdded) return@downloadBitmap
                        activeDownload = null
                        Toast.makeText(activity, "Échec du téléchargement", Toast.LENGTH_SHORT).show()
                        finishWorkingSheet()
                    }
                )
            } else {
                finishWorkingSheet()
            }
        } else {
            Toast.makeText(activity, Constants.JUST_DOWNLOAD_FR, Toast.LENGTH_SHORT).show()
            finishWorkingSheet()
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

    fun fadeToUp(view: View, spaceWithPixel: Float, duration: Long) {

        // Set button alpha to the 0
        view.alpha = 0F
        view.translationY = spaceWithPixel

        // Animate the alpha value to 1F and set duration
        view.animate().alpha(1F).translationYBy(-spaceWithPixel).setDuration(duration)
    }
}
