package tahadeta.example.mascover.ui.home

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.FirebaseFirestore
import tahadeta.example.mascover.BuildConfig
import tahadeta.example.mascover.R
import tahadeta.example.mascover.data.Categorie
import tahadeta.example.mascover.util.updateShown

class HomeFragment : Fragment() {

    var listCategories: MutableList<Categorie> = ArrayList()
    private var actualVersion = BuildConfig.VERSION_NAME

    private lateinit var categorieAdapter: CategorieAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeBack: View
    private lateinit var favouriteBack: View
    private lateinit var settingBack: View
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
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = root.findViewById(R.id.listCategories)
        homeBack = root.findViewById(R.id.homeBack)
        favouriteBack = root.findViewById(R.id.favouriteBack)
        settingBack = root.findViewById(R.id.settingBack)
        animationView = root.findViewById(R.id.animationLoading)

        initComponent()

        return root
    }

    private fun initComponent() {

        listCategories = ArrayList()
        getCtegories()
        getApplicationVersion()

        settingBack.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }

        favouriteBack.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }
    }

    private fun getApplicationVersion() {
        var database = FirebaseFirestore.getInstance()
        database.collection("version").document("version").get()
            .addOnCompleteListener {
                val version = it.result!!["versionNumber"]
                if (!version!!.equals(actualVersion)) {
                    if (!updateShown) {
                        updateShown = true
                        openPopupUpdate(it.result!!["storeLink"].toString())
                    }
                }
            }
    }

    private fun openPopupUpdate(link: String) {
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.update_popup_dialog)
        var update_Btn = dialog.findViewById<TextView>(R.id.update_Btn)
        var cancel_Btn = dialog.findViewById<TextView>(R.id.cancel_Btn)

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

    fun getCtegories() {

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

                    categorieAdapter = CategorieAdapter(this.context, listCategories)
                    listCategories.sortBy { it.weight }
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
