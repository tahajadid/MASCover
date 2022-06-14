package tahadeta.example.mascover.ui.wallpaper

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.WallpaperHelper
import java.io.File
import java.io.IOException

class DetailWallpaperFragment : Fragment() {

    private val args by navArgs<DetailWallpaperFragmentArgs>()
    lateinit var setWallpaperView: View
    lateinit var likeImage: ImageView
    lateinit var dislikeImage: ImageView
    lateinit var downloadVew: View

    lateinit var imageWallpaper: ImageView
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

        WallpaperHelper.setImage(imageWallpaper, args.idWallpaper)

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

        setWallpaperImage()
    }

    private fun downloadImage(pathImage: String) {

        if (isStoragePermissionGranted()) {
            val refStorage = Firebase.storage.reference.child(pathImage)

            val localFile = File.createTempFile("tempImag", "jpg")
            refStorage.getFile(localFile).addOnCompleteListener {

                val bitmapLast = BitmapFactory.decodeFile(localFile.absolutePath)

                MediaStore.Images.Media.insertImage(
                    requireActivity().getContentResolver(),
                    bitmapLast,
                    pathImage,
                    "Image"
                )
            }
        } else {
            Toast.makeText(requireContext(), "Permission Rejetée, Activez la permission", Toast.LENGTH_LONG)
        }
    }

    @SuppressLint("ResourceType", "MissingPermission")
    private fun setWallpaperImage() {

        val wallpaperManager =
            WallpaperManager.getInstance(context)

        setWallpaperView.setOnClickListener {

            try {
                // set the wallpaper by calling the setResource function and
                // passing the drawable file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setResource(R.drawable.fes_cover, WallpaperManager.FLAG_SYSTEM)
                    // wallpaperManager.setResource(R.drawable.fes_cover,WallpaperManager.FLAG_LOCK)
                }
            } catch (e: IOException) {
                // here the errors can be logged instead of printStackTrace
                e.printStackTrace()
            }
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
}
