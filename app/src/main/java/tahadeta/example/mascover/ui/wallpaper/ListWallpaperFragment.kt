package tahadeta.example.mascover.ui.wallpaper

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.FirebaseFirestore
import tahadeta.example.mascover.R
import tahadeta.example.mascover.data.Wallpaper
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager

class ListWallpaperFragment : Fragment() {

    private val args by navArgs<ListWallpaperFragmentArgs>()
    var listWallpapers: MutableList<Wallpaper> = ArrayList()
    private var wallpaperAdapter: WallpaperAdapter? = null
    private lateinit var recyclerView: RecyclerView
    lateinit var backImage: ImageView
    lateinit var favouriteImage: View
    lateinit var settingImage: View
    private lateinit var chipAll: TextView
    private lateinit var chipNew: TextView
    private lateinit var chipLight: TextView
    private lateinit var chipDark: TextView
    private var selectedFilter = FILTER_ALL

    // get the instance of the animated JSON
    lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_list_wallpaper, container, false)

        if (ModelPreferencesManager.get<Boolean>(Constants.FIRST_CON) == null) {
            ModelPreferencesManager.put(true, Constants.FIRST_CON)
            Log.d("FistTest", "FirstConn..")
        }

        recyclerView = root.findViewById(R.id.listWallpapers)
        animationView = root.findViewById(R.id.animationLoading)
        backImage = root.findViewById(R.id.backImage)
        favouriteImage = root.findViewById(R.id.favouriteBack)
        settingImage = root.findViewById(R.id.settingBack)
        chipAll = root.findViewById(R.id.chipAll)
        chipNew = root.findViewById(R.id.chipNew)
        chipLight = root.findViewById(R.id.chipLight)
        chipDark = root.findViewById(R.id.chipDark)
        chipAll.setOnClickListener { selectFilter(FILTER_ALL) }
        chipNew.setOnClickListener { selectFilter(FILTER_NEW) }
        chipLight.setOnClickListener { selectFilter(FILTER_LIGHT) }
        chipDark.setOnClickListener { selectFilter(FILTER_DARK) }
        updateChips()
        if (listWallpapers.isNotEmpty()) {
            animationView.visibility = View.GONE
            showWallpapers(animate = false)
        }

        // Change image in case of arabic Language
        if (ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH) == false) {
            backImage.setImageResource(R.drawable.right_arrow)
        }

        getWallpapers()

        backImage.setOnClickListener {
            findNavController().navigateUp()
        }

        favouriteImage.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }

        settingImage.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }

        return root
    }

    override fun onDestroyView() {
        recyclerView.adapter = null
        wallpaperAdapter = null
        super.onDestroyView()
    }

    fun getWallpapers() {
        var database = FirebaseFirestore.getInstance()
        database.collection("wallpaper")
            .whereEqualTo("idCategorie", args.idCategorie).get()
            .addOnCompleteListener {
                if (!isAdded || view == null) return@addOnCompleteListener
                if (it.isSuccessful) {
                    listWallpapers = ArrayList()
                    for (result in it.result) {
                        listWallpapers.add(
                            result.toObject(Wallpaper::class.java).copy(idWallpaper = result.id)
                        )
                        Log.d("FirestoreData2", "Suss = " + result.toString())
                    }

                    animationView.visibility = View.GONE
                    listWallpapers.sortByDescending {
                        it.numberDownload
                    }
                    showWallpapers(animate = wallpaperAdapter == null)
                }
            }.addOnFailureListener {
                Log.d("FirestoreData2", "Error = " + it.toString())
            }
    }

    private fun selectFilter(filter: String) {
        selectedFilter = filter
        updateChips()
        if (listWallpapers.isNotEmpty() || wallpaperAdapter != null) {
            showWallpapers(animate = false)
        }
    }

    private fun showWallpapers(animate: Boolean) {
        val items = filteredWallpapers()
        val adapter = wallpaperAdapter
        if (adapter == null) {
            wallpaperAdapter = WallpaperAdapter(context, items.toMutableList()).also {
                recyclerView.layoutManager = GridLayoutManager(context, 2)
                recyclerView.adapter = it
            }
            if (animate) recyclerView.scheduleLayoutAnimation()
        } else {
            adapter.submit(items)
        }
    }

    private fun filteredWallpapers(): List<Wallpaper> {
        return when (selectedFilter) {
            FILTER_NEW -> listWallpapers.filter { it.type.equals("NEW", ignoreCase = true) }
            FILTER_LIGHT -> listWallpapers.filter { it.type.equals("LIGHT", ignoreCase = true) }
            FILTER_DARK -> listWallpapers.filter { it.type.equals("DARK", ignoreCase = true) }
            else -> listWallpapers
        }
    }

    private fun updateChips() {
        styleChip(chipAll, selectedFilter == FILTER_ALL)
        styleChip(chipNew, selectedFilter == FILTER_NEW)
        styleChip(chipLight, selectedFilter == FILTER_LIGHT)
        styleChip(chipDark, selectedFilter == FILTER_DARK)
    }

    private fun styleChip(chip: TextView, selected: Boolean) {
        chip.setBackgroundResource(
            if (selected) R.drawable.bg_filter_chip_selected else R.drawable.bg_filter_chip_idle
        )
        chip.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected) R.color.black else R.color.settings_title
            )
        )
        chip.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
    }

    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_NEW = "NEW"
        private const val FILTER_LIGHT = "LIGHT"
        private const val FILTER_DARK = "DARK"
    }
}
