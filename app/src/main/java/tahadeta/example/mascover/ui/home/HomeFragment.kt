package tahadeta.example.mascover.ui.home

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.FirebaseFirestore
import tahadeta.example.mascover.BuildConfig
import tahadeta.example.mascover.R
import tahadeta.example.mascover.data.Categorie
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.Constants.LINK_STORE
import tahadeta.example.mascover.util.ModelPreferencesManager
import tahadeta.example.mascover.util.runAfterLanguagePrompt
import tahadeta.example.mascover.util.updateShown

class HomeFragment : Fragment() {

    var listCategories: MutableList<Categorie> = ArrayList()
    private var actualVersion = BuildConfig.VERSION_NAME

    private lateinit var categorieAdapter: CategorieAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeBack: View
    private lateinit var favouriteBack: View
    private lateinit var flashImage: ImageView
    private lateinit var settingBack: View
    lateinit var animationView: LottieAnimationView
    // lateinit var adView: AdView

    // For Demo
    lateinit var okOne: TextView
    lateinit var okTwo: TextView
    lateinit var demoOneCl: ConstraintLayout
    lateinit var demoTwoCl: ConstraintLayout

    // Ads View
    lateinit var adView: AdView

    var flashIsOne = false

    private lateinit var cameraId: String
    private var notificationPromptShown = false
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = root.findViewById(R.id.listCategories)
        homeBack = root.findViewById(R.id.homeBack)
        favouriteBack = root.findViewById(R.id.favouriteBack)
        settingBack = root.findViewById(R.id.settingBack)
        animationView = root.findViewById(R.id.animationLoading)
        flashImage = root.findViewById(R.id.flash_iv)
        demoOneCl = root.findViewById(R.id.demo_three_cl)
        demoTwoCl = root.findViewById(R.id.demo_four_cl)
        okOne = root.findViewById(R.id.okDemo_one)
        okTwo = root.findViewById(R.id.okDemo_three)
        adView = root.findViewById(R.id.adView)

        initComponent()

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initComponent() {
        // set Banner Ads
        setBannerAds()

        listCategories = ArrayList()
        getCategories()
        getApplicationVersion()

        val window: Window = requireActivity().window

        var cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        if (window != null) {
            window.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.black))
            window.navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.black)
        }

        settingBack.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }

        favouriteBack.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }

        flashImage.visibility = View.GONE
        flashImage.setOnClickListener {
            if (flashIsOne) {
                flashIsOne = false
                flashImage.setImageResource(R.drawable.flash_empty)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false)
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            } else {
                flashIsOne = true
                flashImage.setImageResource(R.drawable.flash_fill)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, true)
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setBannerAds() {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }



    private fun getApplicationVersion() {
        val database = FirebaseFirestore.getInstance()
        database.collection("version").document("version").get()
            .addOnCompleteListener { task ->
                val snapshot = task.result
                val needsUpdate = task.isSuccessful &&
                    snapshot != null &&
                    snapshot.exists() &&
                    snapshot.get("versionNumber")?.toString() != actualVersion
                val storeLink = snapshot?.get("storeLink")?.toString()?.takeIf { it.isNotBlank() }
                    ?: LINK_STORE
                val blocked = snapshot?.get("blocked") as? Boolean ?: false
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isAdded) return@postDelayed
                    runAfterLanguagePrompt {
                        if (!isAdded) return@runAfterLanguagePrompt
                        if (needsUpdate) {
                            if (!updateShown) {
                                updateShown = true
                                openPopupUpdate(storeLink, blocked)
                            }
                        } else {
                            askNotificationPermission()
                        }
                    }
                }, 2000)
            }
    }

    private fun askNotificationPermission() {
        if (notificationPromptShown) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationPromptShown = true
        ModelPreferencesManager.put(true, Constants.NOTIFICATION_ASKED)
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun openPopupUpdate(link: String, isBlocked : Boolean = false) {
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.update_popup_dialog)
        var update_Btn = dialog.findViewById<TextView>(R.id.update_Btn)
        var cancel_Btn = dialog.findViewById<TextView>(R.id.cancel_Btn)

        if(isBlocked) {
            cancel_Btn.visibility = View.GONE
            dialog.setCancelable(false)
        }

        update_Btn.setOnClickListener {
            val uri: Uri =
                Uri.parse(link) // missing 'http://' will cause crashed

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            dialog.dismiss()
        }

        cancel_Btn.setOnClickListener {
            dialog.dismiss()
        }

        val width = resources.displayMetrics.widthPixels * 0.80
        val height = resources.displayMetrics.heightPixels * 0.40

        dialog.window?.setLayout(width.toInt(), height.toInt())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    fun getCategories() {
        var database = FirebaseFirestore.getInstance()
        database.collection("categorie").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (result in it.result) {
                        listCategories.add(result.toObject(Categorie::class.java))
                        Log.d("FirestoreData", "Suss = " + result.toString())
                    }

                    // hide animation
                    animationView.visibility = View.GONE

                    val filteredList = listCategories
                        .filter { it.isVisible }
                        .sortedBy { it.weight }
                        .toMutableList()

                    categorieAdapter = CategorieAdapter(this.context, filteredList)
                    recyclerView.apply {
                        layoutManager = GridLayoutManager(this.context, 1)
                        adapter = categorieAdapter
                    }

                    recyclerView.scheduleLayoutAnimation()
                }
            }.addOnFailureListener {
                Log.d("FirestoreData", "Error = " + it.toString())
            }
    }
}
