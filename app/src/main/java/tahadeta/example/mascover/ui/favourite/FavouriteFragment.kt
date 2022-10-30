package tahadeta.example.mascover.ui.favourite

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.listFavourite

class FavouriteFragment : Fragment() {

    lateinit var homeImage: View
    lateinit var settingImage: View
    lateinit var animationView: LottieAnimationView
    lateinit var emptyTextView: TextView
    lateinit var favouriteListAdapter: FavouriteListAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var adView: AdView
    private lateinit var flashImage: ImageView
    private lateinit var yellowImage: ImageView
    var flashIsOne = false
    private lateinit var cameraId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_favourite, container, false)

        homeImage = root.findViewById(R.id.homeBack)
        settingImage = root.findViewById(R.id.settingBack)
        recyclerView = root.findViewById(R.id.listOfFavouvite)
        animationView = root.findViewById(R.id.animationLoading)
        emptyTextView = root.findViewById(R.id.emptyFavouriteLabel)
        flashImage = root.findViewById(R.id.flash_iv)
        yellowImage = root.findViewById(R.id.yellow_iv)

        val adContainer = root.findViewById<View>(R.id.banner_container) as LinearLayout

        adView = AdView(requireContext(), "602184014891911_602185281558451", AdSize.BANNER_HEIGHT_50)

        adContainer.addView(adView)
        adView.loadAd()

        settingImage.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }

        homeImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        initComponent()
        return root
    }

    private fun initComponent() {

        var cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        if (!listFavourite.isEmpty()) {
            animationView.visibility = View.GONE
            emptyTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            getWallpapers()
        }

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

        yellowImage.setOnClickListener {
            findNavController().navigate(R.id.yellowScreenFragment)
        }
    }

    fun getWallpapers() {

        favouriteListAdapter =
            FavouriteListAdapter(this.context, listFavourite)
        recyclerView.apply {
            layoutManager = GridLayoutManager(this.context, 2)
            adapter = favouriteListAdapter
        }
    }
}
