package tahadeta.example.mascover.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tahadeta.example.mascover.R

class SettingFragment : Fragment() {

    lateinit var homeImage: View
    lateinit var favouriteImage: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_setting, container, false)

        homeImage = root.findViewById(R.id.homeBack)
        favouriteImage = root.findViewById(R.id.favouriteBack)

        favouriteImage.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }

        homeImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        return root
    }
}
