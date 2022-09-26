package tahadeta.example.mascover.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        if (!listFavourite.isEmpty()) {
            animationView.visibility = View.GONE
            emptyTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            getWallpapers()
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
