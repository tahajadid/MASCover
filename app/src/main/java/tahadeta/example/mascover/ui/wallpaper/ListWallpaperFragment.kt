package tahadeta.example.mascover.ui.wallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var backImage: ImageView
    lateinit var favouriteImage: View
    lateinit var settingImage: View

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

    fun getWallpapers() {
        var database = FirebaseFirestore.getInstance()
        database.collection("wallpaper")
            .whereEqualTo("idCategorie", args.idCategorie).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    listWallpapers = ArrayList()
                    for (result in it.result) {
                        listWallpapers.add(result.toObject(Wallpaper::class.java))
                        Log.d("FirestoreData2", "Suss = " + result.toString())
                    }

                    // hide animation
                    animationView.visibility = View.GONE
                    listWallpapers.sortByDescending {
                        it.numberDownload
                    }
                    wallpaperAdapter = WallpaperAdapter(this.context, listWallpapers)

                    recyclerView.apply {
                        layoutManager = GridLayoutManager(this.context, 2)
                        adapter = wallpaperAdapter
                    }
                    recyclerView.scheduleLayoutAnimation()

                    wallpaperAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                Log.d("FirestoreData2", "Error = " + it.toString())
            }
    }
}
